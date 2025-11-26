package io.github.jonloucks.gradle.kit;


import io.github.jonloucks.variants.api.Environment;
import org.gradle.api.GradleException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.Base64;

import static io.github.jonloucks.contracts.api.Checks.nullCheck;
import static java.nio.charset.StandardCharsets.UTF_8;

final class Internal {
    private Internal() {
        throw new AssertionError("Utility class can't be instantiated.");
    }
  
    static String base64Encode(String text) {
        return new String(Base64.getEncoder().encode(text.getBytes(UTF_8)), UTF_8);
    }
    
    static String base64Decode(String text) {
        return new String(Base64.getDecoder().decode(text), UTF_8);
    }
    
    static void createChecksums(File file) {
        final File validFile = nullCheck(file, "File must be present.");
        createMD5Checksum(validFile);
        createSHA1Checksum(validFile);
    }
    
    static void uploadBundle(Environment environment, String bundleName, File bundleFile) {
        new UploadBundleImpl(environment, bundleName, bundleFile).upload();
    }
    
    private static void createSHA1Checksum(File file) {
        generateChecksum(file, new File(file.getAbsolutePath() + ".sha1"), "SHA1");
    }
    
    private static void createMD5Checksum(File file) {
        generateChecksum(file, new File(file.getAbsolutePath() + ".md5"), "MD5");
    }
    
    private static void generateChecksum(File inputFile, File outputFile, String algorithm) {
        try {
            writeDigestBytes(outputFile, createDigestBytes(inputFile, algorithm));
        } catch (Exception thrown) {
            throw new GradleException("Unable to generate checksums.", thrown);
        }
    }
    
    private static void writeDigestBytes(File outputFile, byte[] digestBytes) throws Exception {
        final StringBuilder stringBuilder = new StringBuilder();
        for (byte b : digestBytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(stringBuilder.toString().getBytes(UTF_8));
        }
    }
    
    private static byte[] createDigestBytes(File inputFile, String algorithm) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance(algorithm);
        
        try (FileInputStream inputStream = new FileInputStream(inputFile)) {
            final byte[] buffer = new byte[1024];
            int bytesCount;
            while ((bytesCount = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesCount);
            }
            return digest.digest();
        }
    }
}
