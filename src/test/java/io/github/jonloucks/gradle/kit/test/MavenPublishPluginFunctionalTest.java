package io.github.jonloucks.gradle.kit.test;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

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

    @Test
    public void run_WithDefaults_Works() {
        final Map<String,String> environment = new HashMap<>();
        environment.put("kit.ossrh.username", "dry-run");
        environment.put("kit.ossrh.password", "dry-run");
  
        BuildResult result = new KitGradleRunner()
            .withPlugins(JAVA_LIBRARY_KIT, MAVEN_PUBLISH_KIT)
            .withEnvironment(environment)
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("build", "publish", "createPublisherBundle", "uploadPublisherBundle")
            .build();
        
        final String output = result.getOutput();
        
        assertNotNull(output);
        assertThat(output, containsString("Applying maven-publish plugin..."));
        assertThat(output, not(containsString("Applying signing plugin...")));
    }
    
    @Test
    public void run_WithBadCredentials_Works() {
        final Map<String,String> environment = new HashMap<>();
        environment.put("kit.ossrh.username", "invalid-test-user");
        environment.put("kit.ossrh.password", "invalid-test-password");
        
        final GradleRunner gradleRunner = new KitGradleRunner()
            .withPlugins(JAVA_LIBRARY_KIT, MAVEN_PUBLISH_KIT)
            .withEnvironment(environment)
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("build", "publish", "createPublisherBundle", "uploadPublisherBundle");
        final BuildResult result = gradleRunner.buildAndFail();
        
        final String output = result.getOutput();
        
        assertNotNull(output);
        assertThat(output, containsString("Failure response"));
        assertThat(output, containsString("Applying maven-publish plugin..."));
        assertThat(output, not(containsString("Applying signing plugin...")));
    }
    
    @Test
    public void run_WithUnknownUrl_Works() {
        
        final Map<String,String> environment = new HashMap<>();
        environment.put("kit.ossrh.username", "invalid-test-user");
        environment.put("kit.ossrh.password", "invalid-test-password");
        environment.put("kit.ossrh.url", "https://invalid-url");
     
        final GradleRunner gradleRunner = new KitGradleRunner()
            .withPlugins(JAVA_LIBRARY_KIT, MAVEN_PUBLISH_KIT)
            .withEnvironment(environment)
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("build", "publish", "createPublisherBundle", "uploadPublisherBundle");
        
        final BuildResult result = gradleRunner.buildAndFail();
        
        final String output = result.getOutput();
        
        assertNotNull(output);
        assertThat(output, containsString("Unable connect to"));
        assertThat(output, containsString("Applying maven-publish plugin..."));
        assertThat(output, not(containsString("Applying signing plugin...")));
    }
}
