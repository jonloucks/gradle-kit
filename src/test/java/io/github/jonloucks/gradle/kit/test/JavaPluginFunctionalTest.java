package io.github.jonloucks.gradle.kit.test;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static io.github.jonloucks.gradle.kit.test.Constants.*;
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
        Path projectDir =  ProjectDeployer.deploy(JAVA_KIT);

        // Run the build
        final BuildResult result = GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("tasks")
            .withProjectDir(projectDir.toFile())
            .build();
        
        final String output = result.getOutput();
        
        assertNotNull(output);
        // Verify the result
        assertThat(output, containsString("Applying java plugin..."));
        assertThat(output, containsString("Applying jacoco plugin..."));
        assertThat(output, containsString("Applying javadoc..."));
        assertThat(output, containsString("Applying spotbugs plugin..."));
        assertThat(output, not(containsString("Applying maven-publish plugin...")));
        assertThat(output, not(containsString("Applying signing plugin...")));
    }
}
