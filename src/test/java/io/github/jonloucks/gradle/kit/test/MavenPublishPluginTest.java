package io.github.jonloucks.gradle.kit.test;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import io.github.jonloucks.gradle.kit.MavenPublishPlugin;
import static io.github.jonloucks.gradle.kit.test.Internal.MAVEN_PUBLISH_KIT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public final class MavenPublishPluginTest {
    
    @Test
    public void plugin_Constructor() {
        assertDoesNotThrow(MavenPublishPlugin::new);
    }
  
    @Test
    public void plugin_PublishMavenIsApplied() {
        final Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply(MAVEN_PUBLISH_KIT);
        
        assertDoesNotThrow(() -> {
            project.getTasks().named("publishToMavenLocal");
        });
    }
}
