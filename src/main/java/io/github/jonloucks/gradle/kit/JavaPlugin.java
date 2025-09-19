package io.github.jonloucks.gradle.kit;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Extension of the Gradle 'java-library' plugin
 */
public final class JavaPlugin implements Plugin<@NotNull Project> {
    
    /**
     * Invoked via reflection by Gradle
     */
    public JavaPlugin() {
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
            new JavaVersioningApplier(project).apply();
        }
        
        private void applyJavaLibraryPlugin() {
            System.out.println("Applying java plugin...");
            project.getPlugins().apply("java");
        }
        
        private final Project project;
    }
}