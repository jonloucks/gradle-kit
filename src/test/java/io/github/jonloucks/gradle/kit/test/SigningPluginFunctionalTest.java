package io.github.jonloucks.gradle.kit.test;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.github.jonloucks.gradle.kit.test.Constants.JAVA_KIT;
import static io.github.jonloucks.gradle.kit.test.Constants.SIGNING_KIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("functionalTest")
public class SigningPluginFunctionalTest {

    @Test
    public void run_WithDefaults_Works() {
        final BuildResult result = new KitGradleRunner()
            .withPlugins(JAVA_KIT, SIGNING_KIT)
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("tasks")
            .build();
        
        final String output = result.getOutput();
        
        assertNotNull(output);
        assertThat(output, containsString("Applying signing plugin..."));
        assertThat(output, not(containsString("Applying maven-publish plugin...")));
    }
    
    @Test
    public void run_WithSecrets_Works() {
        final Map<String,String> environment = new HashMap<>();
        environment.put("kit.ossrh.gpg.secret.key", "ZHJ5LXJ1bg==");
        environment.put("kit.ossrh.gpg.secret.key.password", "dry-run");
      
        BuildResult result = new KitGradleRunner()
            .withPlugins(JAVA_KIT, SIGNING_KIT)
            .withEnvironment(environment)
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("tasks")
            .build();
        
        final String output = result.getOutput();
        
        assertNotNull(output);
        assertThat(output, containsString("Applying signing plugin..."));
        assertThat(output, not(containsString("Applying maven-publish plugin...")));
    }
    
    @Test
    public void run_WithKeyAndNoPassword_Works() {
        final Map<String,String> environment = new HashMap<>();
        environment.put("kit.ossrh.gpg.secret.key", "ZHJ5LXJ1bg==");
        
        final BuildResult result = new KitGradleRunner()
            .withPlugins(JAVA_KIT, SIGNING_KIT)
            .withEnvironment(environment)
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("tasks")
            .build();
        
        final String output = result.getOutput();
        
        assertNotNull(output);
        assertThat(output, containsString("Applying signing plugin..."));
        assertThat(output, not(containsString("Applying maven-publish plugin...")));
    }
}
