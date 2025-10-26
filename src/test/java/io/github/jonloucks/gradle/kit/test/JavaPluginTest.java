package io.github.jonloucks.gradle.kit.test;

import io.github.jonloucks.gradle.kit.JavaPlugin;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.gradle.kit.test.Internal.JAVA_KIT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public final class JavaPluginTest extends JavaPluginTestBase {
    
    public JavaPluginTest() {
        super(JAVA_KIT);
    }
    
    @Test
    public void plugin_Constructor() {
        assertDoesNotThrow(JavaPlugin::new);
    }
}
