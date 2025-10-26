package io.github.jonloucks.gradle.kit;

import org.gradle.api.Project;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.MinimalJavadocOptions;

@SuppressWarnings("CodeBlock2Expr")
final class JavadocApplier extends ProjectApplier {
    JavadocApplier(Project project) {
        super(project);
    }
    
    @Override
    void apply() {
        log("Applying javadoc...");
        
        getProject().afterEvaluate(project -> {
            project.getTasks().withType(Javadoc.class).configureEach( javadoc -> {
                javadoc.setFailOnError(true);
                javadoc.getModularity().getInferModulePath().set(true);
                javadoc.options(MinimalJavadocOptions::showFromPublic);
            });
        });
    }
}
