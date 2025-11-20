package io.github.jonloucks.gradle.kit;

import org.junit.jupiter.api.Test;

import static io.github.jonloucks.gradle.kit.test.Tools.assertInstantiateThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class InternalTests {
    @Test
    public void internal_encoding() {
        final String input = "Hello World!";
        final String output = Internal.base64Decode(Internal.base64Encode(input));
        
        assertEquals(input, output);
    }
    
    @Test
    public void internal_Instantiate_Throws() {
        assertInstantiateThrows(Internal.class);
    }
}
