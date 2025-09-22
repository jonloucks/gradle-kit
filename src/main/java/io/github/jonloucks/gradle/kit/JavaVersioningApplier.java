package io.github.jonloucks.gradle.kit;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static io.github.jonloucks.gradle.kit.Internal.getConfig;
import static io.github.jonloucks.gradle.kit.Internal.log;
import static java.util.Optional.ofNullable;

@SuppressWarnings("CodeBlock2Expr")
final class JavaVersioningApplier {
    
    JavaVersioningApplier(Project project) {
        this.project = project;
    }
    
    void apply() {
        configureJavaVersions();
        configureTestTaggingRules();
    }
    
    private void configureJavaVersions() {
        log("Applying Java versions ...");
        configureJavaPlugin();
        configureAllJavaCompiles();
    }
    
    private void configureAllJavaCompiles() {
        forAllJavaCompiles(this::configureJavaCompile);
    }
    
    private void forAllJavaCompiles(Consumer<JavaCompile> consumer) {
        project.getTasks().withType(JavaCompile.class).configureEach(consumer::accept);
    }
    
    private void configureJavaCompile(JavaCompile compile) {
        compile.getOptions().getRelease().set(getTargetVersion().asInt());
        final List<String> compilerArgs = new ArrayList<>(compile.getOptions().getCompilerArgs());
        compilerArgs.add("-Xlint:all");
        compile.getOptions().setCompilerArgs(compilerArgs);
    }
    
    private void configureJavaPlugin() {
        final JavaPluginExtension javaPlugin = project.getExtensions().getByType(JavaPluginExtension.class);
        javaPlugin.setSourceCompatibility(getSourceVersion());
        javaPlugin.setTargetCompatibility(getTargetVersion());
        javaPlugin.getToolchain().getLanguageVersion().set(getCompilerVersion());
        javaPlugin.withJavadocJar();
        javaPlugin.withSourcesJar();
    }
    
    private JavaLanguageVersion getCompilerVersion() {
        final String text = getConfig(project, "kit.java.compiler.version", null);
        if (null == text || text.isEmpty()) {
            return JavaLanguageVersion.of(17);
        }
        return JavaLanguageVersion.of(Integer.parseInt(text));
    }
    
    private JavaLanguageVersion getTargetVersion() {
        final String text = getConfig(project, "kit.java.target.version", null);
        if (null == text || text.isEmpty()) {
            return getSourceVersion();
        }
        
        return JavaLanguageVersion.of(Integer.parseInt(text));
    }
    
    private JavaLanguageVersion getSourceVersion() {
        final String text = getConfig(project, "kit.java.source.version", null);
        
        if (null == text || text.isEmpty()) {
            return JavaLanguageVersion.of(9);
        }
        
        return JavaLanguageVersion.of(Integer.parseInt(text));
    }
    
    private String[] splitTagsProperty(String propertyName) {
        if (project.hasProperty(propertyName)) {
            final Object value = project.findProperty(propertyName);
            if (ofNullable(value).isPresent()) {
                return value.toString().split(",");
            }
        }
        return new String[0];
    }
    
    private void configureTestTaggingRules() {
        log("Applying Test Tagging Rules ...");
        
        configureDefaultTestTask();
        
        registerTaggedTestTask("integration", "unstable", "slow", "functional");
        registerTaggedTestTask("functional", "unstable", "slow", "integration");
    }
    
    private void registerTaggedTestTask(String includeTag, String... excludeTags) {
        final String taskName = includeTag + "Test";
        log("Creating " + taskName + "...");
        
        final TaskProvider<@NotNull Test> taggedTaskProvider = project.getTasks().register(taskName, TEST_TYPE, task -> {
            log("Configuring " + taskName + ".");
            task.setDescription("Runs tests with tag: " + includeTag);
            task.setGroup("verification");
            
            task.useJUnitPlatform(options -> {
                options.includeTags(includeTag);
                options.excludeTags(getPropertyTags("excludeTags", excludeTags));
            });
            
            final SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
            final SourceSet mainTestSourceSet = sourceSets.stream()
                .filter(ss -> ss.getName().equals("test"))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No test source set found"));
            
            task.setTestClassesDirs(mainTestSourceSet.getOutput().getClassesDirs());
            task.setClasspath(mainTestSourceSet.getRuntimeClasspath());
            task.shouldRunAfter("test");
//            System.out.println("Configured " + taskName + ".");
        });
        
        project.getTasks().named("check").configure(task -> task.dependsOn(taggedTaskProvider));
    }
    
    private String[] getPropertyTags(String propertyName, String... defaults) {
        if (project.hasProperty(propertyName)) {
            return splitTagsProperty(propertyName);
        }
        return defaults;
    }
    
    private void configureDefaultTestTask() {
        project.getTasks().named("test", TEST_TYPE).configure(task -> {
            task.useJUnitPlatform(configure -> {
                configure.includeTags(getPropertyTags("includeTags"));
                configure.excludeTags(getPropertyTags("excludeTags",
                    "unstable", "slow", "integration", "functional"));
            });
        });
    }
    
    private static final Class<Test> TEST_TYPE = Test.class;
    private final Project project;
}

