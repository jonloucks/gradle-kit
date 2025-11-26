package io.github.jonloucks.gradle.kit;

import io.github.jonloucks.variants.api.Environment;
import io.github.jonloucks.variants.api.Variant;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;

import java.util.Optional;

import static io.github.jonloucks.variants.api.GlobalVariants.createEnvironment;

abstract class ProjectApplier {
    ProjectApplier(Project project) {
        this.project = project;
        this.environment = createEnvironment( b -> b //
            .addSystemEnvironmentSource() //
            .addSource(this::projectEnvironmentVariable) //
            .addSystemPropertiesSource() //
            .addSource(this::projectProperty) //
        );
    }

    abstract void apply();
    
    final Project getProject() {
        return project;
    }
    
    final boolean isRootProject() {
        return getProject().getRootProject().equals(getProject());
    }
    
    final boolean isTestProject() {
        return isTestProject(getProject());
    }
    
    final boolean isTestProject(Project project) {
        return project.getName().endsWith("-test") || project.getName().endsWith("-tests");
    }

    final <T> Optional<T> getConfig(Variant<T> variant) {
        return environment.findVariance(variant);
    }
    
    final <T> T requireConfig(Variant<T> variant) {
        return environment.getVariance(variant);
    }
    
    final void log(String text) {
        if (isLogEnabled()) {
            System.out.println(text);
        }
    }
    
    final boolean isLogEnabled() {
        return environment.findVariance(Configs.KIT_LOG_ENABLED).orElse(false);
    }
    
    final Environment getEnvironment() {
        return environment;
    }
    
    private Optional<CharSequence> projectProperty(String key) {
        return Optional.ofNullable(project.findProperty(key)).map(Object::toString);
    }
    
    private Optional<CharSequence> projectEnvironmentVariable(String key) {
        final Provider<String> variable = project.getProviders().environmentVariable(key);
        if (variable.isPresent()) {
            return Optional.of(variable.get());
        } else {
            return Optional.empty();
        }
    }
    
    
    private final Project project;
    private final Environment environment;
}
