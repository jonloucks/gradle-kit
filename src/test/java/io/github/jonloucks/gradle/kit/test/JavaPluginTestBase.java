package io.github.jonloucks.gradle.kit.test;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public abstract class JavaPluginTestBase {
    
    public JavaPluginTestBase(String pluginName) {
        this.pluginName = pluginName;
    }
    
    @Test
    public void plugin_JavaAndJdkVersions_Defaults() {
        final Project project = ProjectBuilder.builder()
            .withName("some-impl")
            .build();
    
        project.getPlugins().apply(pluginName);
        project.evaluationDependsOn(":");
        
        final JavaPluginExtension javaPlugin = project.getExtensions().getByType(JavaPluginExtension.class);
        
        assertThat(javaPlugin.getSourceCompatibility(), equalTo(JavaVersion.toVersion("9")));
        assertThat(javaPlugin.getTargetCompatibility(), equalTo(JavaVersion.toVersion("9")));
        assertThat(javaPlugin.getToolchain().getLanguageVersion().get(), equalTo(JavaLanguageVersion.of(17)));

        for (JavaCompile javaCompile : project.getTasks().withType(JavaCompile.class)) {
            assertThat(javaCompile.getOptions().getRelease().get(), equalTo(JavaLanguageVersion.of(9).asInt()));
        }
    }
    
    @Test
    public void plugin_JavaAndJdkVersions_Overrides() {
        final Project project = ProjectBuilder.builder()
            .withName("some-api")
            .build();
        final BiConsumer<String,String> ext = project.getExtensions().getExtraProperties()::set;
        
        ext.accept("KIT_JAVA_SOURCE_VERSION", JAVA_SOURCE_VERSION.toString());
        ext.accept("KIT_JAVA_TARGET_VERSION", JAVA_TARGET_VERSION.toString() );
        ext.accept("KIT_JAVA_TEST_SOURCE_VERSION", JAVA_TEST_SOURCE_VERSION.toString());
        ext.accept("KIT_JAVA_TEST_TARGET_VERSION", JAVA_TEST_TARGET_VERSION.toString());
        ext.accept("KIT_JAVA_COMPILER_VERSION", JAVA_COMPILER_VERSION.toString());
        project.getPlugins().apply(pluginName);
        project.evaluationDependsOn(":");
        
        final JavaPluginExtension javaPlugin = project.getExtensions().getByType(JavaPluginExtension.class);
        
        assertThat(javaPlugin.getSourceCompatibility(), equalTo(JavaVersion.toVersion(JAVA_SOURCE_VERSION.toString())));
        assertThat(javaPlugin.getTargetCompatibility(), equalTo(JavaVersion.toVersion(JAVA_TARGET_VERSION.toString())));
        assertThat(javaPlugin.getToolchain().getLanguageVersion().get(), equalTo(JAVA_COMPILER_VERSION));

        for (JavaCompile javaCompile : project.getTasks().withType(JavaCompile.class)) {
            assertThat(javaCompile.getOptions().getRelease().get(),
                either(equalTo(JAVA_TARGET_VERSION.asInt())).or(equalTo(JAVA_TEST_TARGET_VERSION.asInt())));
        }
    }
    
    @Test
    public void plugin_JavaAndJdkVersions_WithTestProject_Overrides() {
        final Project project = ProjectBuilder.builder()
            .withName("some-test")
            .build();
        final BiConsumer<String,String> ext = project.getExtensions().getExtraProperties()::set;
        
        ext.accept("KIT_JAVA_SOURCE_VERSION", JAVA_SOURCE_VERSION.toString());
        ext.accept("KIT_JAVA_TARGET_VERSION", JAVA_TARGET_VERSION.toString() );
        ext.accept("KIT_JAVA_TEST_SOURCE_VERSION", JAVA_TEST_SOURCE_VERSION.toString());
        ext.accept("KIT_JAVA_TEST_TARGET_VERSION", JAVA_TEST_TARGET_VERSION.toString());
        ext.accept("KIT_JAVA_COMPILER_VERSION", JAVA_COMPILER_VERSION.toString());
        project.getPlugins().apply(pluginName);
        project.evaluationDependsOn(":");
        
        final JavaPluginExtension javaPlugin = project.getExtensions().getByType(JavaPluginExtension.class);
        
        assertThat(javaPlugin.getSourceCompatibility(), equalTo(JavaVersion.toVersion(JAVA_TEST_SOURCE_VERSION.toString())));
        assertThat(javaPlugin.getTargetCompatibility(), equalTo(JavaVersion.toVersion(JAVA_TEST_TARGET_VERSION.toString())));
        assertThat(javaPlugin.getToolchain().getLanguageVersion().get(), equalTo(JAVA_COMPILER_VERSION));
    }
    
    @Test
    @Tag("integration")
    public void plugin_TestTagging() {
        final Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply(pluginName);
        project.evaluationDependsOn(":");
        
        assertDoesNotThrow(()-> {
            project.getTasks().named("integrationTest");
            project.getTasks().named("functionalTest");
            project.getTasks().named("jacocoTestReport");
            project.getTasks().named("spotbugsMain");
        });
    }
    
    private final String pluginName;
    
    private static final JavaLanguageVersion JAVA_SOURCE_VERSION = JavaLanguageVersion.of(8);
    private static final JavaLanguageVersion JAVA_TARGET_VERSION = JavaLanguageVersion.of(9);
    private static final JavaLanguageVersion JAVA_TEST_SOURCE_VERSION = JavaLanguageVersion.of(11);
    private static final JavaLanguageVersion JAVA_TEST_TARGET_VERSION = JavaLanguageVersion.of(13);
    private static final JavaLanguageVersion JAVA_COMPILER_VERSION = JavaLanguageVersion.of(21);
}
