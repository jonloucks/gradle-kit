package io.github.jonloucks.gradle.kit.test;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.gradle.kit.test.Constants.JAVA_KIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("functionalTest")
public class JavaPluginFunctionalTest {

    @Test
    public void run_WithDefaults() {
        final BuildResult result = new KitGradleRunner()
            .withPlugins(JAVA_KIT)
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("tasks")
            .build();
        
        final String output = result.getOutput();
        
        assertNotNull(output);
        assertThat(output, containsString("Applying java plugin..."));
        assertThat(output, containsString("Applying jacoco plugin..."));
        assertThat(output, containsString("Applying javadoc..."));
        assertThat(output, containsString("Applying spotbugs plugin..."));
        assertThat(output, not(containsString("Applying maven-publish plugin...")));
        assertThat(output, not(containsString("Applying signing plugin...")));
    }
}
