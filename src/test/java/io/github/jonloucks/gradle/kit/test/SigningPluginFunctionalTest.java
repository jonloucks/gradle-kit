package io.github.jonloucks.gradle.kit.test;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static io.github.jonloucks.gradle.kit.test.Constants.JAVA_KIT;
import static io.github.jonloucks.gradle.kit.test.Constants.SIGNING_KIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("functionalTest")
public class SigningPluginFunctionalTest {
    
    @BeforeEach
    public void beforeEachTest() {
        System.setProperty("gradle.kit.log.enabled", "true");
    }
    
    @AfterEach
    public void afterEachTest() {
        System.setProperty("gradle.kit.log.enabled", "false");
    }
    
    @Test
    public void run_WithDefaults_Works() throws Throwable {
        final Path projectFolder = ProjectDeployer.deploy(JAVA_KIT, SIGNING_KIT);

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
    }
}
