package io.github.jonloucks.gradle.kit;

import org.gradle.api.Project;

final class JavaPluginApplier extends ProjectApplier {
    JavaPluginApplier(Project project) {
        super(project);
    }
    
    @Override
    void apply() {
        applyOnce(() -> {
            log("Applying java plugin...");
            getProject().getPlugins().apply("java");
        });
    }
}
