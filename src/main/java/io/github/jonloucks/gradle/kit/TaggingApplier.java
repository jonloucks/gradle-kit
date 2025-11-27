package io.github.jonloucks.gradle.kit;

import io.github.jonloucks.variants.api.Variant;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.testing.Test;

import java.util.Optional;

import static io.github.jonloucks.gradle.kit.Configs.*;

final class TaggingApplier extends ProjectApplier {
    
    TaggingApplier(Project project) {
        super(project);
    }
    
    @Override
    void apply() {
        getProject().afterEvaluate(x -> {
            log("Applying Test Tagging Rules ...");
            
            configureStandardTestTasks();
            
            registerTaggedTestTask("integration", KIT_INTEGRATION_EXCLUDE_TAGS);
            registerTaggedTestTask("functional", KIT_FUNCTIONAL_EXCLUDE_TAGS);
        });
    }
    
    private void registerTaggedTestTask(String includeTag, Variant<String[]> excludeVariant) {
        final String taskName = includeTag + "Test";
        log("Creating " + taskName + "...");
        
        final TaskProvider<Test> taggedTaskProvider = getProject().getTasks().register(taskName, TEST_TYPE, task -> {
            log("Configuring " + taskName + ".");
            task.setDescription("Runs tests with tag: " + includeTag);
            task.setGroup("verification");
            
            task.useJUnitPlatform(options -> {
                options.includeTags(includeTag);
                options.excludeTags(requireConfig(excludeVariant));
            });
            
            findTestSourceSet().ifPresent(sourceSet -> {
                task.setTestClassesDirs(sourceSet.getOutput().getClassesDirs());
                task.setClasspath(sourceSet.getRuntimeClasspath());
            });
            
            task.shouldRunAfter("test");
            log("Configured " + taskName + ".");
        });
        
        getProject().getTasks().named("check").configure(task -> task.dependsOn(taggedTaskProvider));
    }
    
    private Optional<SourceSet> findTestSourceSet() {
        return getProject()
            .getExtensions() //
            .getByType(SourceSetContainer.class) //
            .stream() //
            .filter(ss -> ss.getName().equals("test")) //
            .findFirst();
    }
    
    private void configureStandardTestTasks() {
        getProject().getTasks().named("test", TEST_TYPE).configure(task -> { //
            task.useJUnitPlatform(configure -> {
                configure.includeTags(requireConfig(KIT_INCLUDE_TAGS));
                configure.excludeTags(requireConfig(KIT_EXCLUDE_TAGS));
            });
        });
    }
 
    private static final Class<Test> TEST_TYPE = Test.class;
}
