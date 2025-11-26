package io.github.jonloucks.gradle.kit;

import io.github.jonloucks.variants.api.Environment;
import org.gradle.api.GradleException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import static io.github.jonloucks.contracts.test.Tools.assertInstantiateThrows;
import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static io.github.jonloucks.variants.api.GlobalVariants.createEnvironment;
import static java.util.Collections.singletonMap;
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
    public void internal_createChecksums_WithFile_Works() throws Throwable {
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
    public void internal_uploadBundle_WithNullEnvironment_Throws() throws Throwable {
        final File file = createBundleFile();

        assertThrown(IllegalArgumentException.class,
            () -> Internal.uploadBundle(null, "x", file),
            "Environment must be present.");
    }
    
    @Test
    public void internal_uploadBundle_WithNullBundleName_Throws() throws Throwable {
        final File file = createBundleFile();
        final Environment environment = createEnvironment(b -> {});
        
        assertThrown(IllegalArgumentException.class,
            () -> Internal.uploadBundle(environment, null, file),
            "Bundle name must be present.");
    }
    
    @Test
    public void internal_uploadBundle_WithNullBundleFile_Throws() {
        final Environment environment = createEnvironment(b -> {});
        
        assertThrown(IllegalArgumentException.class,
            () -> Internal.uploadBundle(environment, "x", null),
            "Bundle file must be present.");
    }
    
    @Test
    public void internal_uploadBundle_WithBundleFileDoesNotExist_Throws() {
        final File file = new File(UUID.randomUUID()+".zip");
        final Environment environment = createEnvironment(b -> {});
        
        assertThrown(GradleException.class,
            () -> Internal.uploadBundle(environment, "x", file));
    }

    @Test
    public void internal_uploadBundle_WithMissingAuthorName_Throws() throws Throwable {
        final File file = createBundleFile();
        final Map<String,String> map = new HashMap<>();
        map.put("kit.ossrh.password", "dry-run");
        final Environment environment = createEnvironment(b -> b.addMapSource(map));
        
        assertThrown(GradleException.class,
            () -> Internal.uploadBundle(environment, "x", file),
            "Author username must be present.");
    }
    
    @Test
    public void internal_uploadBundle_WithMissingAuthorPassword_Throws() throws Throwable {
        final File file = createBundleFile();
        final Environment environment = createEnvironment(b -> b.addMapSource(singletonMap("kit.ossrh.username", "dry-run")));
        
        assertThrown(GradleException.class,
            () -> Internal.uploadBundle(environment, "x", file),
            "Author password must be present.");
    }
    
    @Test
    public void internal_uploadBundle_WithDryRun_Works() throws Throwable {
        final File file = createBundleFile();
        final Map<String,String> map = new HashMap<>();
        map.put("kit.ossrh.username", "dry-run");
        map.put("kit.ossrh.password", "dry-run");
        final Environment environment = createEnvironment(b -> b.addMapSource(map));
        
        Internal.uploadBundle(environment, "x", file);
    }
    
    private static File createBundleFile() throws Throwable {
        return File.createTempFile("kit_", ".zip");
    }
    
    @Test
    public void internal_Instantiate_Throws() {
        assertInstantiateThrows(Internal.class);
    }
}
