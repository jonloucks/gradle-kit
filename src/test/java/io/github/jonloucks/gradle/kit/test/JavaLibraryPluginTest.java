package io.github.jonloucks.gradle.kit.test;

import io.github.jonloucks.gradle.kit.JavaLibraryPlugin;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.github.jonloucks.gradle.kit.test.Internal.JAVA_LIBRARY_KIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public final class JavaLibraryPluginTest {

    @Test
    public void plugin_Constructor() {
        assertDoesNotThrow(JavaLibraryPlugin::new);
    }
    
    @Test
    public void plugin_JavaAndJdkVersions() {
        final Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply(JAVA_LIBRARY_KIT);
        
        final JavaPluginExtension javaPlugin = project.getExtensions().getByType(JavaPluginExtension.class);
        
        assertThat(javaPlugin.getSourceCompatibility(), equalTo(JavaVersion.toVersion(SOURCE_VERSION)));
        assertThat(javaPlugin.getTargetCompatibility(), equalTo(JavaVersion.toVersion(TARGET_VERSION)));
        assertThat(javaPlugin.getToolchain().getLanguageVersion().get(), equalTo(JDK_VERSION));
        
        for (JavaCompile javaCompile : project.getTasks().withType(JavaCompile.class)) {
            assertThat(javaCompile.getOptions().getRelease().get(), equalTo(SOURCE_VERSION.asInt()));
        }
    }
    
    @Test
    @Tag("integration")
    public void plugin_TestTagging() {
        final Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply(JAVA_LIBRARY_KIT);
        
        assertDoesNotThrow(()-> {
            project.getTasks().named("integrationTest");
            project.getTasks().named("functionalTest");
        });
    }
    
    private static final JavaLanguageVersion SOURCE_VERSION = JavaLanguageVersion.of(9);
    private static final JavaLanguageVersion TARGET_VERSION = JavaLanguageVersion.of(9);
    private static final JavaLanguageVersion JDK_VERSION = JavaLanguageVersion.of(17);
    
}
