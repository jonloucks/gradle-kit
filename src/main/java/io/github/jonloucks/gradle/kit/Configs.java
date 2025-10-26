package io.github.jonloucks.gradle.kit;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.provider.Provider;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static io.github.jonloucks.gradle.kit.Internal.base64Decode;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;

final class Configs {
    
    static <T> T requireConfig(Project project, Config<T> config) {
        return getConfig(project, config).orElseThrow(() -> getConfigException(project, config));
    }

    static <T> Optional<T> getConfig(Project project, Config<T> config) {
        for (String name : config.getKeys()) {
            final Optional<String> value = findConfig(project, name);
            if (value.isPresent()) {
                return config.of(value.get());
            }
        }
        if (config.getLink().isPresent()) {
            Optional<T> linkValue = getConfig(project, config.getLink().get());
            if (linkValue.isPresent()) {
                return linkValue;
            }
        }
        return config.getFallback();
    }
    
    static <T> String formatConfig(Config<T> config) {
        final StringBuilder builder = new StringBuilder();
        config.getName().ifPresent(builder::append);
        config.getDescription().ifPresent(d -> builder.append(" : ").append(d));
        return builder.toString();
    }
    
    static final Config<Boolean> KIT_LOG_ENABLED = new ConfigImpl<>(Boolean::parseBoolean)
        .name("KIT_LOG_ENABLED")
        .keys("KIT_LOG_ENABLED", "kit.log.enabled", "gradle.kit.log.enabled")
        .fallback(() -> false);
    
    static final Config<JavaLanguageVersion> KIT_JAVA_COMPILER_VERSION = new ConfigImpl<>(JavaLanguageVersion::of)
        .name("Java Compiler Version")
        .keys( "KIT_JAVA_COMPILER_VERSION", "kit.java.compiler.version")
        .fallback(() -> JavaLanguageVersion.of("17"));
    
    static final Config<JavaLanguageVersion> KIT_JAVA_SOURCE_VERSION =
        new ConfigImpl<>(JavaLanguageVersion::of)
        .name("Java Source Version")
        .keys( "KIT_JAVA_SOURCE_VERSION", "kit.java.source.version")
        .fallback(() -> JavaLanguageVersion.of("9"));
    
    static final Config<JavaLanguageVersion> KIT_JAVA_TARGET_VERSION = new ConfigImpl<>(JavaLanguageVersion::of)
        .name("Java Target Version")
        .keys( "KIT_JAVA_TARGET_VERSION", "kit.java.target.version")
        .fallback(KIT_JAVA_SOURCE_VERSION);
    
    static final Config<JavaLanguageVersion> KIT_JAVA_TEST_SOURCE_VERSION = new ConfigImpl<>(JavaLanguageVersion::of)
        .name("Java Test Source Version")
        .keys( "KIT_JAVA_TEST_SOURCE_VERSION", "kit.java.test.source.version")
        .fallback(KIT_JAVA_SOURCE_VERSION);
    
    static final Config<JavaLanguageVersion> KIT_JAVA_TEST_TARGET_VERSION = new ConfigImpl<>(JavaLanguageVersion::of)
        .name("Java Test Target Version")
        .keys( "KIT_JAVA_TEST_TARGET_VERSION", "kit.java.test.target.version")
        .fallback(KIT_JAVA_TEST_SOURCE_VERSION);
    
    static final Config<String> KIT_PROJECT_WORKFLOW = new ConfigImpl<>(identity())
        .name("Project Workflow")
        .keys( "KIT_PROJECT_WORKFLOW", "PROJECT_WORKFLOW", "kit.project.workflow")
        .fallback(() -> "unknown");
    
    static final Config<String> KIT_OSSRH_USERNAME = new ConfigImpl<>(identity())
        .name("Kit OSSRH User Login Name")
        .keys("KIT_OSSRH_USERNAME", "OSSRH_USERNAME", "kit.ossrh.username");
    
    static final Config<String> KIT_OSSRH_PASSWORD = new ConfigImpl<>(identity())
        .name("Kit OSSRH Password")
        .keys( "KIT_OSSRH_PASSWORD", "OSSRH_PASSWORD", "kit.ossrh.password");
    
    static final Config<String> KIT_GPG_SECRET_KEY = new ConfigImpl<>(Configs::ofSecretKey)
        .name("Kit OSSRH GPG Secret Key")
        .keys( "KIT_OSSRH_GPG_SECRET_KEY", "OSSRH_GPG_SECRET_KEY", "kit.ossrh.gpg.secret.key");
    
    static final Config<String> KIT_GPG_SECRET_KEY_PASSWORD = new ConfigImpl<>(identity())
        .name("Kit OSSRH GPG Secret Key Password")
        .keys("KIT_OSSRH_GPG_SECRET_KEY_PASSWORD", "OSSRH_GPG_SECRET_KEY_PASSWORD", "kit.ossrh.gpg.secret.key.password");
    
    private static GradleException getConfigException(Project project, Config<?> config) {
        return new GradleException("Missing config for project " + project.getName() + " " + formatConfig(config));
    }
    
    private static String ofSecretKey(String text) {
        if (ofNullable(text).isPresent()) {
            if (text.isEmpty()) {
                return null;
            }
            if (text.startsWith("-")) {
                return text;
            }
            try {
                return base64Decode(text);
            } catch (IllegalArgumentException thrown) {
                throw new GradleException("Invalid gpg secret key.");
            }
        }
        return null;
    }
    
    private static Optional<String> findConfig(Project project, String name) {
        final Provider<@NotNull String> variable = project.getProviders().environmentVariable(name);
        if (variable.isPresent()) {
            return Optional.of(variable.get());
        }
        return ofNullable(project.findProperty(name))
            .or(() -> findGlobalConfig(name))
            .map(Object::toString);
    }
    
    private static Optional<String> findGlobalConfig(String name) {
        try {
            final Object systemValue = System.getProperty(name);
            if (ofNullable(systemValue).isPresent()) {
                return Optional.of(systemValue.toString());
            }
            return ofNullable(System.getenv(name));
        } catch (SecurityException ignored) {
            return Optional.empty();
        }
    }
    
    private Configs() {
        throw new AssertionError("Utility class");
    }
}
