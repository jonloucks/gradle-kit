package io.github.jonloucks.gradle.kit.test;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.jonloucks.gradle.kit.test.Internal.SIGNING_KIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("functionalTest")
public class SigningPluginFunctionalTest {
    
    @Test
    public void run_WithDefaults_Works() throws Throwable {
        final Path projectFolder = Paths.get("build/functionalTest");
        Files.createDirectories(projectFolder);
        writeString(projectFolder.resolve("settings.gradle"), "");
        writeString(projectFolder.resolve("build.gradle"),
            "plugins {" +
                "  id('"+SIGNING_KIT+"')" +
                "}");
        
        // Run the build
        BuildResult result = GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("tasks")
            .withProjectDir(projectFolder.toFile())
            .build();
        
        final String output = result.getOutput();
        
        assertNotNull(output);
        assertThat(output, containsString("Applying signing plugin..."));
        assertThat(output, not(containsString("Applying maven-publish plugin...")));
        assertThat(output, not(containsString("Applying java plugin...")));
        assertThat(output, not(containsString("Applying java-library plugin...")));
    }
    
    private void writeString(Path path, String string) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            writer.write(string);
        }
    }
}
