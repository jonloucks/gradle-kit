package io.github.jonloucks.gradle.kit;

import io.github.jonloucks.variants.api.Environment;
import io.github.jonloucks.variants.api.Variant;
import org.gradle.api.GradleException;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.github.jonloucks.gradle.kit.Configs.*;
import static io.github.jonloucks.gradle.kit.Internal.base64Encode;
import static io.github.jonloucks.gradle.kit.test.Tools.assertInstantiateThrows;
import static io.github.jonloucks.variants.api.GlobalVariants.createEnvironment;
import static org.junit.jupiter.api.Assertions.*;

public final class ConfigsTests {
    @Test
    public void configs_Instantiate_Throws() {
        assertInstantiateThrows(Configs.class);
    }
    
    
    @Test
    public void configs_getConfig_JavaVersion_WithNullValue_Works() {
        final Optional<JavaLanguageVersion> optional = withGetConfig(KIT_JAVA_TEST_TARGET_VERSION, null);
        
        assertTrue(optional.isPresent());
    }
    
    @Test
    public void configs_getConfig_JavaVersion_WithRealValue_Works() {
        final Optional<JavaLanguageVersion> optional = withGetConfig(KIT_JAVA_TEST_TARGET_VERSION, "21");
        
        assertTrue(optional.isPresent());
        assertEquals(21, optional.get().asInt());
    }
    
    @Test
    public void configs_getConfig_WithEmptyValue_Works() {
        final Optional<String> optional = withGetConfig(KIT_GPG_SECRET_KEY, "");
        
        assertFalse(optional.isPresent());
    }
    
    @Test
    public void configs_getConfig_WithEncodedValue_Works() {
        final Optional<String> optional = withGetConfig(KIT_GPG_SECRET_KEY, base64Encode("Hello World!"));

        assertTrue(optional.isPresent());
        assertEquals("Hello World!", optional.get());
    }
    
    @Test
    public void configs_getConfig_KeyFormatValue_Works() {
        final Optional<String> optional = withGetConfig(KIT_GPG_SECRET_KEY, "-Hello World!");
        
        assertTrue(optional.isPresent());
        assertEquals("-Hello World!", optional.get());
    }
    
    @Test
    public void configs_getConfig_WithBadFormat_Throws() {
        final GradleException thrown = assertThrows(GradleException.class,
            () -> withGetConfig(KIT_GPG_SECRET_KEY, "Hello World!"));
   
        assertNotNull(thrown);
        assertNotNull(thrown.getMessage());
    }
    
    private static <T> Optional<T> withGetConfig(final Variant<T> variant, final String input) {
        final Map<String,String> map = new HashMap<>();
        for (String key : variant.getKeys()) {
            map.put(key, input);
        }
        
        final Environment environment = createEnvironment(b -> b.addMapSource(map));
        
        return environment.findVariance(variant);
    }
}
