package io.github.jonloucks.gradle.kit;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.github.jonloucks.gradle.kit.Internal.base64Encode;
import static io.github.jonloucks.gradle.kit.test.Tools.assertInstantiateThrows;
import static org.junit.jupiter.api.Assertions.*;

public final class ConfigsTests {
    @Test
    public void configs_Instantiate_Throws() {
        assertInstantiateThrows(Configs.class);
    }
    
    @Test
    public void configs_formatConfig_Works() {
        assertNotNull(Configs.formatConfig(Configs.KIT_GPG_SECRET_KEY));
    }
    
    @Test
    public void configs_requireConfig_Works() {
        final Project project = ProjectBuilder.builder()
            .withName("some-impl")
            .build();
        final GradleException thrown = assertThrows(GradleException.class,
            () -> Configs.requireConfig(project, Configs.KIT_GPG_SECRET_KEY));
        assertNotNull(thrown);
        assertNotNull(thrown.getMessage());
    }
    
    @Test
    public void configs_getConfig_WithEmptyValue_Works() {
        final Optional<String> optional = withGetConfig(Configs.KIT_GPG_SECRET_KEY, "");
        
       assertFalse(optional.isPresent());
    }
    
    @Test
    public void configs_getConfig_WithEncodedValue_Works() {
        final Optional<String> optional = withGetConfig(Configs.KIT_GPG_SECRET_KEY, base64Encode("Hello World!"));

        assertTrue(optional.isPresent());
        assertEquals("Hello World!", optional.get());
    }
    
    @Test
    public void configs_getConfig_KeyFormatValue_Works() {
        final Optional<String> optional = withGetConfig(Configs.KIT_GPG_SECRET_KEY, "-Hello World!");
        
        assertTrue(optional.isPresent());
        assertEquals("-Hello World!", optional.get());
    }
    
    @Test
    public void configs_getConfig_WithBadFormat_Throws() {
        final GradleException thrown = assertThrows(GradleException.class,
            () -> withGetConfig(Configs.KIT_GPG_SECRET_KEY, "Hello World!"));
   
        assertNotNull(thrown);
        assertNotNull(thrown.getMessage());
    }
    
    private static Optional<String> withGetConfig(final Config<String> config, final String input) {
        final Project project = ProjectBuilder.builder().build();
        
        project.getExtensions().getExtraProperties().set(config.getKeys().get(0), input);
        
        return Configs.getConfig(project, config);
    }
}
