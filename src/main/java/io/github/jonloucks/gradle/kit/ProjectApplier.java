package io.github.jonloucks.gradle.kit;

import io.github.jonloucks.variants.api.Environment;
import io.github.jonloucks.variants.api.Variant;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.provider.Provider;

import java.util.Optional;

import static io.github.jonloucks.variants.api.GlobalVariants.createEnvironment;
import static java.lang.Boolean.TRUE;

abstract class ProjectApplier {
    ProjectApplier(Project project) {
        this.project = project;
        this.applierName = getClass().getName();
        this.environment = createEnvironment( b -> b //
            .addSystemEnvironmentSource() //
            .addSource(this::projectEnvironmentVariable) //
            .addSystemPropertiesSource() //
            .addSource(this::projectProperty) //
        );
    }

    abstract void apply();
    
    final void applyOnce(Runnable block) {
        final String name = applierName;
        final ExtraPropertiesExtension extension = project.getExtensions().getExtraProperties();
        if (extension.has(name) &&
            TRUE.equals(extension.get(name))) {
            log("Project " + name + " is already applied");
        } else {
            extension.set(name, TRUE);
            try {
                block.run();
            } catch (Exception thrown) {
                extension.set(name, thrown);
                throw thrown;
            }
        }
    }
    
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
    private final String applierName;
}
