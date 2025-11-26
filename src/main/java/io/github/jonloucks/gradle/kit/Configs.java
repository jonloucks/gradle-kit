package io.github.jonloucks.gradle.kit;

import io.github.jonloucks.variants.api.Variant;
import org.gradle.api.GradleException;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

import static io.github.jonloucks.gradle.kit.Internal.base64Decode;
import static io.github.jonloucks.variants.api.GlobalVariants.createVariant;
import static java.util.Optional.ofNullable;

final class Configs {
    
    static final Variant<Boolean> KIT_LOG_ENABLED = createVariant(b -> b //
        .name("Kit Log Enabled") //
        .keys("KIT_LOG_ENABLED", "kit.log.enabled", "gradle.kit.log.enabled") //
        .parser(t -> Boolean.parseBoolean(t.toString()))
        .fallback(() -> false) //
        .description("Enable or Disable Kit Logging"));
    
    static final Variant<JavaLanguageVersion> KIT_JAVA_COMPILER_VERSION = createVariant(b -> b //
        .name("Java Compiler Version") //
        .keys("KIT_JAVA_COMPILER_VERSION", "kit.java.compiler.version") //
        .parser(c -> JavaLanguageVersion.of(c.toString())) //
        .fallback(() -> JavaLanguageVersion.of("17")));
    
    static final Variant<JavaLanguageVersion> KIT_JAVA_SOURCE_VERSION = createVariant(b -> b //
        .name("Java Source Version") //
        .keys( "KIT_JAVA_SOURCE_VERSION", "kit.java.source.version") //
        .parser(c -> JavaLanguageVersion.of(c.toString())) //
        .fallback(() -> JavaLanguageVersion.of("9")));
    
    static final Variant<JavaLanguageVersion> KIT_JAVA_TARGET_VERSION = createVariant(b -> b //
        .name("Java Target Version") //
        .keys( "KIT_JAVA_TARGET_VERSION", "kit.java.target.version") //
        .parser(c -> JavaLanguageVersion.of(c.toString())) //
        .link(KIT_JAVA_SOURCE_VERSION));
    
    static final Variant<JavaLanguageVersion> KIT_JAVA_TEST_SOURCE_VERSION = createVariant(b -> b //
        .name("Java Test Source Version") //
        .keys( "KIT_JAVA_TEST_SOURCE_VERSION", "kit.java.test.source.version") //
        .parser(c -> JavaLanguageVersion.of(c.toString())) //
        .link(KIT_JAVA_SOURCE_VERSION));
    
    static final Variant<JavaLanguageVersion> KIT_JAVA_TEST_TARGET_VERSION = createVariant(b -> b //
        .name("Java Test Target Version") //
        .keys( "KIT_JAVA_TEST_TARGET_VERSION", "kit.java.test.target.version") //
        .parser(c -> JavaLanguageVersion.of(c.toString())) //
        .link(KIT_JAVA_TEST_SOURCE_VERSION));
    
    static final Variant<String> KIT_PROJECT_WORKFLOW = createVariant(b -> b //
        .name("Project Workflow") //
        .keys( "KIT_PROJECT_WORKFLOW", "PROJECT_WORKFLOW", "kit.project.workflow") //
        .parser(CharSequence::toString) //
        .fallback(() -> "unknown")
    );
    
    static final Variant<String> KIT_OSSRH_URL = createVariant(b -> b //
        .name("Kit OSSRH URL") //
        .keys("KIT_OSSRH_URL", "kit.ossrh.url") //
        .parser(CharSequence::toString) //
        .fallback(() -> "https://central.sonatype.com/api/v1/publisher/upload?publishingType=USER_MANAGED") //
    );
    
    static final Variant<String> KIT_OSSRH_USERNAME = createVariant(b -> b //
        .name("Kit OSSRH User Login Name") //
        .keys("KIT_OSSRH_USERNAME", "OSSRH_USERNAME", "kit.ossrh.username")
        .parser(CharSequence::toString) //
    );
    
    static final Variant<String> KIT_OSSRH_PASSWORD = createVariant(b -> b //
        .name("Kit OSSRH Password") //
        .keys( "KIT_OSSRH_PASSWORD", "OSSRH_PASSWORD", "kit.ossrh.password") //
        .parser(CharSequence::toString) //
    );
    
    static final Variant<String> KIT_GPG_SECRET_KEY = createVariant(b -> b //
        .name("Kit OSSRH GPG Secret Key")
        .keys("KIT_OSSRH_GPG_SECRET_KEY", "OSSRH_GPG_SECRET_KEY", "kit.ossrh.gpg.secret.key")
        .parser(Configs::ofSecretKey)
    );
    
    static final Variant<String> KIT_GPG_SECRET_KEY_PASSWORD = createVariant(b -> b //
        .name("Kit OSSRH GPG Secret Key Password") //
        .keys("KIT_OSSRH_GPG_SECRET_KEY_PASSWORD", "OSSRH_GPG_SECRET_KEY_PASSWORD", "kit.ossrh.gpg.secret.key.password") //
        .parser(CharSequence::toString)
    );

    private static String ofSecretKey(CharSequence chars) {
        if (ofNullable(chars).isPresent()) {
            final String text = chars.toString();
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

    private Configs() {
        throw new AssertionError("Utility class");
    }
}
