package io.github.jonloucks.gradle.kit;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Extension of the Gradle 'java-library' plugin
 */
public final class JavaPlugin implements Plugin<Project> {
    
    /**
     * Invoked via reflection by Gradle
     */
    public JavaPlugin() {
    }
    
    @Override
    public void apply(Project project) {
        new JavaPluginApplier(project).apply();
        new JavaVersioningApplier(project).apply();
        new TaggingApplier(project).apply();
        new JacocoApplier(project).apply();
        new SpotBugsApplier(project).apply();
        new JavadocApplier(project).apply();
    }
}