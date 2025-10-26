package io.github.jonloucks.gradle.kit;

import org.gradle.api.Project;

import java.util.Optional;
import java.util.function.Supplier;

abstract class ProjectApplier {
    ProjectApplier(Project project) {
        this.project = project;
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

    final <T> T requireConfig(Config<T> config) {
        return Configs.requireConfig(getProject(), config);
    }
    
    final <T> Optional<T> getConfig(Config<T> config) {
        return Configs.getConfig(getProject(), config);
    }
    
    final void log(Supplier<String> messageSupplier) {
        if (isLogEnabled()) {
            System.out.println(messageSupplier.get());
        }
    }
    
    final void log(String text) {
        if (isLogEnabled()) {
            System.out.println(text);
        }
    }
    
    final boolean isLogEnabled() {
        return getConfig(Configs.KIT_LOG_ENABLED).orElse(false);
    }

    private final Project project;
}
