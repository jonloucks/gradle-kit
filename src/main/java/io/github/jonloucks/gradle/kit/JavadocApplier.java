package io.github.jonloucks.gradle.kit;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.MinimalJavadocOptions;

import static io.github.jonloucks.gradle.kit.Internal.log;

final class JavadocApplier {
    JavadocApplier(Project project) {
        this.targetProject = project;
    }
    
    void apply() {
        log("Applying javadoc...");
        
        targetProject.afterEvaluate(project -> {
            targetProject.getTasks().withType(Javadoc.class).configureEach( javadoc -> {
                javadoc.setFailOnError(true);
                javadoc.getModularity().getInferModulePath().set(true);
                javadoc.options(MinimalJavadocOptions::showFromPublic);
            });
        });
 
    }

    private final Project targetProject;
}
