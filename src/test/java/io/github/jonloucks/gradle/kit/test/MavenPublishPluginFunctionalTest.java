package io.github.jonloucks.gradle.kit.test;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static io.github.jonloucks.gradle.kit.test.Constants.JAVA_LIBRARY_KIT;
import static io.github.jonloucks.gradle.kit.test.Constants.MAVEN_PUBLISH_KIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("functionalTest")
public class MavenPublishPluginFunctionalTest {
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
        Path projectDir =  ProjectDeployer.deploy(JAVA_LIBRARY_KIT, MAVEN_PUBLISH_KIT);
        
        final Map<String,String> environment = new HashMap<>();
        environment.put("kit.ossrh.username", "dry-run");
        environment.put("kit.ossrh.password", "dry-run");
        // Run the build
        BuildResult result = GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withEnvironment(environment)
            .withArguments("build", "publish", "createPublisherBundle", "uploadPublisherBundle")
            .withProjectDir(projectDir.toFile())
            .build();
        
        final String output = result.getOutput();
        
        assertNotNull(output);
        // Verify the result
        assertThat(output, containsString("Applying maven-publish plugin..."));
        assertThat(output, not(containsString("Applying signing plugin...")));
    }
}
