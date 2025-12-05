package io.github.jonloucks.gradle.kit;

import io.github.jonloucks.variants.api.Parsers;
import io.github.jonloucks.variants.api.Variant;
import org.gradle.api.GradleException;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static io.github.jonloucks.gradle.kit.Internal.base64Decode;
import static io.github.jonloucks.variants.api.Checks.textCheck;
import static io.github.jonloucks.variants.api.GlobalVariants.createVariant;

final class Configs {
    
    static final Variant<Boolean> KIT_LOG_ENABLED = createVariant((b,p) -> b //
        .name("Kit Log Enabled") //
        .keys("KIT_LOG_ENABLED", "kit.log.enabled", "gradle.kit.log.enabled") //
        .of(p.ofBoolean())
        .fallback(() -> false) //
        .description("Enable or Disable Kit Logging"));
    
    static final Variant<JavaLanguageVersion> KIT_JAVA_COMPILER_VERSION = createVariant((b,p) -> b //
        .name("Kit Java Compiler Version") //
        .keys("KIT_JAVA_COMPILER_VERSION", "kit.java.compiler.version") //
        .of(ofJavaLanguageVersion(p)) //
        .fallback(() -> JavaLanguageVersion.of("17")));
    
    static final Variant<JavaLanguageVersion> KIT_JAVA_SOURCE_VERSION = createVariant((b,p) -> b //
        .name("Kit Java Source Version") //
        .keys( "KIT_JAVA_SOURCE_VERSION", "kit.java.source.version") //
        .of(ofJavaLanguageVersion(p)) //
        .fallback(() -> JavaLanguageVersion.of("9")));
    
    static final Variant<JavaLanguageVersion> KIT_JAVA_TARGET_VERSION = createVariant((b,p) -> b //
        .name("Kit Java Target Version") //
        .keys( "KIT_JAVA_TARGET_VERSION", "kit.java.target.version") //
        .of(ofJavaLanguageVersion(p)) //
        .link(KIT_JAVA_SOURCE_VERSION));
    
    static final Variant<JavaLanguageVersion> KIT_JAVA_TEST_SOURCE_VERSION = createVariant((b,p) -> b //
        .name("Kit Java Test Source Version") //
        .keys( "KIT_JAVA_TEST_SOURCE_VERSION", "kit.java.test.source.version") //
        .of(ofJavaLanguageVersion(p)) //
        .link(KIT_JAVA_SOURCE_VERSION));
    
    static final Variant<JavaLanguageVersion> KIT_JAVA_TEST_TARGET_VERSION = createVariant((b,p) -> b //
        .name("Kit Java Test Target Version") //
        .keys( "KIT_JAVA_TEST_TARGET_VERSION", "kit.java.test.target.version") //
        .of(ofJavaLanguageVersion(p)) //
        .link(KIT_JAVA_TEST_SOURCE_VERSION));
    
    static final Variant<String> KIT_PROJECT_WORKFLOW = createVariant((b,p) -> b //
        .name("Kit Project Workflow") //
        .keys( "KIT_PROJECT_WORKFLOW", "PROJECT_WORKFLOW", "kit.project.workflow") //
        .of(p.ofString()) //
        .fallback(() -> "unknown")
    );
    
    static final Variant<String> KIT_OSSRH_URL = createVariant((b,p) -> b //
        .name("Kit OSSRH URL") //
        .keys("KIT_OSSRH_URL", "kit.ossrh.url") //
        .of(p.ofString()) //
        .fallback(() -> "https://central.sonatype.com/api/v1/publisher/upload?publishingType=USER_MANAGED") //
    );
    
    static final Variant<String> KIT_OSSRH_USERNAME = createVariant((b,p) -> b //
        .name("Kit OSSRH User Login Name") //
        .keys("KIT_OSSRH_USERNAME", "OSSRH_USERNAME", "kit.ossrh.username")
        .of(p.ofString()) //
    );
    
    static final Variant<String> KIT_OSSRH_PASSWORD = createVariant((b,p) -> b //
        .name("Kit OSSRH Password") //
        .keys( "KIT_OSSRH_PASSWORD", "OSSRH_PASSWORD", "kit.ossrh.password") //
        .of(p.ofString()) //
    );
    
    static final Variant<String> KIT_GPG_SECRET_KEY = createVariant((b,p) -> b //
        .name("Kit OSSRH GPG Secret Key")
        .keys("KIT_OSSRH_GPG_SECRET_KEY", "OSSRH_GPG_SECRET_KEY", "kit.ossrh.gpg.secret.key")
        .of(p.ofTrimAndSkipEmpty(Configs::parseSecretKey))
    );
    
    static final Variant<String> KIT_GPG_SECRET_KEY_PASSWORD = createVariant((b,p) -> b //
        .name("Kit OSSRH GPG Secret Key Password") //
        .keys("KIT_OSSRH_GPG_SECRET_KEY_PASSWORD", "OSSRH_GPG_SECRET_KEY_PASSWORD", "kit.ossrh.gpg.secret.key.password") //
        .of(p.ofString()) //
    );
    
    static final Variant<String[]> KIT_INCLUDE_TAGS = createVariant((b,p) -> b //
        .name("Kit Java Test Include Tags") //
        .keys("KIT_INCLUDE_TAGS", "kit.include.tags", "includeTags") //
        .of(ofStringArray(p)) //
        .fallback(() -> new String[0])
    );

    static final Variant<String[]> KIT_EXCLUDE_TAGS = createVariant((b,p) -> b //
        .name("Kit Java Test Exclude Tags") //
        .keys("KIT_EXCLUDE_TAGS", "kit.exclude.tags", "excludeTags") //
        .of(ofStringArray(p)) //
        .fallback(() -> new String[] { "unstable", "slow", "integration", "functional"})
    );
    
    static final Variant<String[]> KIT_INTEGRATION_EXCLUDE_TAGS = createVariant((b,p) -> b //
        .name("Kit Java Integration Exclude Tags") //
        .keys("KIT_INTEGRATION_EXCLUDE_TAGS", "kit.integration.exclude.tags", "excludeIntegrationTags") //
        .of(ofStringArray(p)) //
        .link(KIT_EXCLUDE_TAGS) //
        .fallback(() -> new String[] { "unstable", "slow", "functional" })
    );
    
    static final Variant<String[]> KIT_FUNCTIONAL_EXCLUDE_TAGS = createVariant((b,p) -> b //
        .name("Kit Java Functional Exclude Tags") //
        .keys("KIT_FUNCTIONAL_EXCLUDE_TAGS", "kit.functional.exclude.tags") //
        .of(ofStringArray(p)) //
        .link(KIT_EXCLUDE_TAGS) //
        .fallback(() -> new String[] { "unstable", "slow", "integration" })
    );
    
    private static Function<CharSequence,Optional<String[]>> ofStringArray(Parsers parsers) {
        final Function<CharSequence,Optional<List<String>>> ofList = parsers.ofList(parsers.ofString(), ",");
        
        return text -> ofList.apply(text).map(strings -> strings.toArray(new String[0]));
    }
    
    private static Function<CharSequence, Optional<JavaLanguageVersion>> ofJavaLanguageVersion(Parsers parsers) {
        return parsers.ofTrimAndSkipEmpty(parsers.string(JavaLanguageVersion::of));
    }
    
    private static String parseSecretKey(CharSequence text) {
        final CharSequence validText = textCheck(text);
        final String string = validText.toString();
  
        if (string.startsWith("-")) {
            return string;
        }
        try {
            return base64Decode(string);
        } catch (IllegalArgumentException thrown) {
            throw new GradleException("Invalid gpg secret key.");
        }
    }

    private Configs() {
        throw new AssertionError("Utility class");
    }
}
