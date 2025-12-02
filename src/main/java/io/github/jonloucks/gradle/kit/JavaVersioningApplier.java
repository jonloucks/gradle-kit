package io.github.jonloucks.gradle.kit;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

import java.util.Optional;
import java.util.function.Consumer;

import static io.github.jonloucks.gradle.kit.Configs.*;
import static io.github.jonloucks.gradle.kit.Internal.adjustCompileArguments;

@SuppressWarnings("CodeBlock2Expr")
final class JavaVersioningApplier extends ProjectApplier {
    
    JavaVersioningApplier(Project project) {
        super(project);
    }
    
    @Override
    void apply() {
        applyOnce(() -> {
            log("Applying Java versions ...");
            
            getProject().afterEvaluate(x -> {
                configureJavaVersions();
            });
        });
    }
    
    private void configureJavaVersions() {
        configureJavaPlugin();
        configureAllJavaCompiles();
        configureTestJavaCompiles();
    }
    
    private void configureTestJavaCompiles() {
        if (isTestProject()) {
            return;
        }
        forAllTestJavaCompiles(this::configureTestJavaCompile);
    }
    
    private void configureAllJavaCompiles() {
        forAllJavaCompiles(this::configureJavaCompile);
    }

    private void forAllTestJavaCompiles(Consumer<JavaCompile> consumer) {
        getProject().getTasks().named("compileTestJava", JavaCompile.class).configure(consumer::accept);
    }
    
    private void forAllJavaCompiles(Consumer<JavaCompile> consumer) {
        getProject().getTasks().withType(JavaCompile.class).configureEach(consumer::accept);
    }
    
    private void configureJavaCompile(JavaCompile compile) {
        getReleaseVersion().ifPresent(x -> compile.getOptions().getRelease().set(x.asInt()));
        compile.getOptions().setCompilerArgs(adjustCompileArguments(compile.getOptions().getCompilerArgs()));
    }
    
    private void configureTestJavaCompile(JavaCompile compile) {
        getTestReleaseVersion().ifPresent(x -> compile.getOptions().getRelease().set(x.asInt()));
    }
    
    private void configureJavaPlugin() {
        final JavaPluginExtension javaPlugin = getProject().getExtensions().getByType(JavaPluginExtension.class);
        javaPlugin.getModularity().getInferModulePath().set(true);
        getSourceCompatibility().ifPresent(javaPlugin::setSourceCompatibility);
        getTargetCompatibility().ifPresent(javaPlugin::setTargetCompatibility);
        getCompilerVersion().ifPresent(javaPlugin.getToolchain().getLanguageVersion()::set);
        javaPlugin.withJavadocJar();
        javaPlugin.withSourcesJar();
    }
    
    private Optional<JavaLanguageVersion> getCompilerVersion() {
        return Optional.of(requireConfig(KIT_JAVA_COMPILER_VERSION));
    }
    
    private Optional<JavaLanguageVersion> getSourceCompatibility() {
        if (isTestProject()) {
            return Optional.of(requireConfig(KIT_JAVA_TEST_SOURCE_VERSION));
        } else {
            return Optional.of(requireConfig(KIT_JAVA_SOURCE_VERSION));
        }
    }
    
    private Optional<JavaLanguageVersion> getTargetCompatibility() {
        if (isTestProject()) {
            return Optional.of(requireConfig(KIT_JAVA_TEST_TARGET_VERSION));
        } else {
            return Optional.of(requireConfig(KIT_JAVA_TARGET_VERSION));
        }
    }
    
    private Optional<JavaLanguageVersion> getReleaseVersion() {
        if (isTestProject()) {
            return getTestReleaseVersion();
        } else {
            return Optional.of(requireConfig(KIT_JAVA_TARGET_VERSION));
        }
    }
    
    private Optional<JavaLanguageVersion> getTestReleaseVersion() {
        final JavaLanguageVersion testVersion = requireConfig(KIT_JAVA_TEST_TARGET_VERSION);
        final JavaLanguageVersion implementationVersion = requireConfig(KIT_JAVA_TARGET_VERSION);
        if (testVersion.compareTo(implementationVersion) > 0) {
            return Optional.of(testVersion);
        } else {
            return Optional.of(implementationVersion);
        }
    }
}

