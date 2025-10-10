package io.github.jonloucks.gradle.kit;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import static io.github.jonloucks.gradle.kit.Internal.log;

/**
 * Extension of the Gradle 'java-library' plugin
 */
public final class JavaLibraryPlugin implements Plugin<@NotNull Project> {
    
    /**
     * Invoked via reflection by Gradle
     */
    public JavaLibraryPlugin() {
    }
    
    public void apply(Project project) {
        new Applier(project).apply();
    }
    
    private static final class Applier {
        private Applier(Project project) {
            this.project = project;
        }
        
        private void apply() {
            applyJavaLibraryPlugin();
            project.afterEvaluate(x -> {
                new JavaVersioningApplier(project).apply();
                new JacocoApplier(project).apply();
                new JavadocApplier(project).apply();
            });
        }
        
        private void applyJavaLibraryPlugin() {
            log("Applying java-library plugin...");
            project.getPlugins().apply("java-library");
        }
        
        private final Project project;
    }
}