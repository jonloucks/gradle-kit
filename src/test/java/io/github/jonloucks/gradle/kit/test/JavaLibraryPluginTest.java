package io.github.jonloucks.gradle.kit.test;

import io.github.jonloucks.gradle.kit.JavaLibraryPlugin;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.gradle.kit.test.Internal.JAVA_LIBRARY_KIT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public final class JavaLibraryPluginTest extends JavaPluginTestBase {
    
    public JavaLibraryPluginTest() {
        super(JAVA_LIBRARY_KIT);
    }
    
    @Test
    public void plugin_Constructor() {
        assertDoesNotThrow(JavaLibraryPlugin::new);
    }
}
