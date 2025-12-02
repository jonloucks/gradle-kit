package io.github.jonloucks.gradle.kit;

import org.gradle.api.Project;

final class JavaLibraryPluginApplier extends ProjectApplier {
    JavaLibraryPluginApplier(Project project) {
        super(project);
    }
    
    @Override
    void apply() {
        applyOnce(() -> {
            log("Applying java-library plugin...");
            getProject().getPlugins().apply("java-library");
        });
    }
}
