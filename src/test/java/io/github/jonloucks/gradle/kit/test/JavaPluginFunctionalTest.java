package io.github.jonloucks.gradle.kit.test;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;

import static io.github.jonloucks.gradle.kit.test.Internal.JAVA_KIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("functionalTest")
public class JavaPluginFunctionalTest {
    @BeforeEach
    public void beforeEachTest() {
        System.setProperty("gradle.kit.log.enabled", "true");
    }
    
    @AfterEach
    public void afterEachTest() {
        System.setProperty("gradle.kit.log.enabled", "false");
    }
    
    @Test
    public void run_WithDefaults() throws Throwable {
        final File projectDir = new File("build/functionalTest");
        Files.createDirectories(projectDir.toPath());
        writeString(new File(projectDir, "settings.gradle"), "");
        writeString(new File(projectDir, "build.gradle"),
            "plugins {" +
                "  id('" + JAVA_KIT + "')" +
                "}");
        
        // Run the build
        final BuildResult result = GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("tasks")
            .withProjectDir(projectDir)
            .build();
        
        final String output = result.getOutput();
        
        assertNotNull(output);
        // Verify the result
        assertThat(output, containsString("Applying java plugin..."));
        assertThat(output, not(containsString("Applying maven-publish plugin...")));
        assertThat(output, not(containsString("Applying signing plugin...")));
    }
    
    private void writeString(File file, String string) throws IOException {
        try (Writer writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
