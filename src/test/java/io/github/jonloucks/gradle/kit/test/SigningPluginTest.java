package io.github.jonloucks.gradle.kit.test;

import io.github.jonloucks.gradle.kit.SigningPlugin;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.gradle.kit.test.Constants.SIGNING_KIT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public final class SigningPluginTest {
    
    @Test
    public void plugin_Constructor() {
        assertDoesNotThrow(SigningPlugin::new);
    }
  
    @Test
    public void plugin_PublishMavenIsApplied() {
        final Project project = ProjectBuilder.builder().build();
        
        assertDoesNotThrow(() -> {
            project.getPluginManager().apply(SIGNING_KIT);
            
            project.evaluationDependsOn(":");
        });
    }
}
