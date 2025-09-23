package io.github.jonloucks.gradle.kit;

import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;

final class Internal {
    private Internal() {
    }
    
    static boolean isLogEnabled() {
        return Boolean.getBoolean("gradle.kit.log.enabled");
    }
    
    static void log(Supplier<String> messageSupplier) {
        if (isLogEnabled()) {
            System.out.println(messageSupplier.get());
        }
    }
    
    static void log(String text) {
        if (isLogEnabled()) {
            System.out.println(text);
        }
    }
    
    static String getConfig(Project project, String name) {
        return getConfig(project, name, null);
    }
    
    static boolean isRootProject(Project project) {
        return project.getRootProject().equals(project);
    }
    
    static String getConfig(Project project, String name, String defaultValue) {
        final Provider<@NotNull String> variable = project.getProviders().environmentVariable(name);
        if (variable.isPresent()) {
            return variable.get();
        }
        return ofNullable(project.findProperty(name)).map(Object::toString).orElse(defaultValue);
    }
    
    static String base64Encode(String text) {
        return new String(Base64.getEncoder().encode(text.getBytes(UTF_8)), UTF_8);
    }
    
    static String base64Decode(String text) {
        return new String(Base64.getDecoder().decode(text), UTF_8);
    }
}
