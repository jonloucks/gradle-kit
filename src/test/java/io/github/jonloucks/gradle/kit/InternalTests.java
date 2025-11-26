package io.github.jonloucks.gradle.kit;

import org.gradle.api.GradleException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.Random;
import java.util.UUID;

import static io.github.jonloucks.contracts.test.Tools.assertInstantiateThrows;
import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class InternalTests {
    @Test
    public void internal_encoding() {
        final String input = "Hello World!";
        final String output = Internal.base64Decode(Internal.base64Encode(input));
        
        assertEquals(input, output);
    }
    
    @Test
    public void internal_createChecksums_WithNullFile_Throws() {
        assertThrown( IllegalArgumentException.class,
            () -> Internal.createChecksums(null),
            "File must be present.");
    }
    
    @Test
    public void internal_createChecksums_WithFileNotFound_Throws() {
        assertThrown( GradleException.class,
            () -> Internal.createChecksums(new File(UUID.randomUUID()+".txt")),
            "Unable to generate checksums.");
    }
    
    @Test
    public void internal_createChecksums_WithFile_Works() throws Exception {
        final File file = File.createTempFile("kit_", ".dat");
        final Random random = new Random();
        final int fileLength = Math.max(1, random.nextInt(10_000));
        final byte[] fileContent = new byte[fileLength];
        random.setSeed(System.currentTimeMillis());
        random.nextBytes(fileContent);
        Files.write(file.toPath(), fileContent);
        
        Internal.createChecksums(file);
        
        final File sha1File = new File(file.getAbsolutePath() + ".sha1");
        final File md5File = new File(file.getAbsolutePath() + ".md5");
        assertTrue(sha1File.exists(), "Sha1 file must exist.");
        assertTrue(md5File.exists(), "MD5 file must exist.");
    }
    
    @Test
    public void internal_Instantiate_Throws() {
        assertInstantiateThrows(Internal.class);
    }
}
