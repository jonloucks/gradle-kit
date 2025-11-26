package io.github.jonloucks.gradle.kit;

import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.testing.Test;

import static java.util.Optional.ofNullable;

final class TaggingApplier extends ProjectApplier {
    
    TaggingApplier(Project project) {
        super(project);
    }
    
    @Override
    void apply() {
        getProject().afterEvaluate(x -> {
            
            log("Applying Test Tagging Rules ...");
            
            configureDefaultTestTask();
            
            registerTaggedTestTask("integration", "unstable", "slow", "functional");
            registerTaggedTestTask("functional", "unstable", "slow", "integration");
        });
    }
    
    private void registerTaggedTestTask(String includeTag, String... excludeTags) {
        final String taskName = includeTag + "Test";
        log("Creating " + taskName + "...");
        
        final TaskProvider<Test> taggedTaskProvider = getProject().getTasks().register(taskName, TEST_TYPE, task -> {
            log("Configuring " + taskName + ".");
            task.setDescription("Runs tests with tag: " + includeTag);
            task.setGroup("verification");
            
            task.useJUnitPlatform(options -> {
                options.includeTags(includeTag);
                options.excludeTags(getPropertyTags("excludeTags", excludeTags));
            });
            
            final SourceSetContainer sourceSets = getProject().getExtensions().getByType(SourceSetContainer.class);
            final SourceSet mainTestSourceSet = sourceSets.stream()
                .filter(ss -> ss.getName().equals("test"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No test source set found"));
            
            task.setTestClassesDirs(mainTestSourceSet.getOutput().getClassesDirs());
            task.setClasspath(mainTestSourceSet.getRuntimeClasspath());
            task.shouldRunAfter("test");
            log("Configured " + taskName + ".");
        });
        
        getProject().getTasks().named("check").configure(task -> task.dependsOn(taggedTaskProvider));
    }
    
    private String[] getPropertyTags(String propertyName, String... defaults) {
        if (getProject().hasProperty(propertyName)) {
            return splitTagsProperty(propertyName);
        }
        return defaults;
    }
    
    private void configureDefaultTestTask() {
        getProject().getTasks().named("test", TEST_TYPE).configure(task -> {
            task.useJUnitPlatform(configure -> {
                configure.includeTags(getPropertyTags("includeTags"));
                configure.excludeTags(getPropertyTags("excludeTags",
                    "unstable", "slow", "integration", "functional"));
            });
        });
    }
    
    private String[] splitTagsProperty(String propertyName) {
        if (getProject().hasProperty(propertyName)) {
            final Object value = getProject().findProperty(propertyName);
            if (ofNullable(value).isPresent()) {
                return value.toString().split(",");
            }
        }
        return new String[0];
    }
    
    private static final Class<Test> TEST_TYPE = Test.class;
}
