package io.github.jonloucks.gradle.kit.test;

import com.github.spotbugs.snom.SpotBugsExtension;
import com.github.spotbugs.snom.SpotBugsTask;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.testfixtures.ProjectBuilder;
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension;
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.function.BiConsumer;

import static io.github.jonloucks.contracts.test.Tools.assertObject;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public abstract class JavaPluginTestBase {
    
    public JavaPluginTestBase(String pluginName) {
        this.pluginName = pluginName;
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"library-api", "library-impl", "library-test"})
    public void plugin_JavaAndJdkVersions_Defaults(String projectName) {
        final Project project = ProjectBuilder.builder()
            .withName(projectName)
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
    
    @ParameterizedTest
    @ValueSource(strings = {"library-test", "library-tests"})
    public void plugin_JavaAndJdkVersions_WithTestProject_Defaults(String projectName) {
        final Project project = ProjectBuilder.builder()
            .withName(projectName)
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
    
    @ParameterizedTest
    @ValueSource(strings = {"library", "library-api", "library-impl"})
    public void plugin_JavaAndJdkVersions_Overrides(String projectName) {
        final Project project = ProjectBuilder.builder()
            .withName(projectName)
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
    
    @ParameterizedTest
    @ValueSource(strings = {"library-test", "library-tests"})
    public void plugin_JavaAndJdkVersions_WithTestProject_Overrides(String projectName) {
        final Project project = ProjectBuilder.builder()
            .withName(projectName)
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
    
    @ParameterizedTest
    @ValueSource(strings = {"library-api", "library-impl", "library-test"})
    @Tag("integration")
    public void plugin_Jacoco_WithProjectName(String name) {
        final Project project = ProjectBuilder.builder().withName(name).build();
        project.getPlugins().apply(pluginName);
        project.evaluationDependsOn(":");
        
        final JacocoPluginExtension jacocoExtension = project.getExtensions().getByType(JacocoPluginExtension.class);
        final JacocoReport jacocoTestReport = project.getTasks().named("jacocoTestReport", JacocoReport.class).get();
        final JacocoCoverageVerification verificationReport = project.getTasks().named("jacocoTestCoverageVerification", JacocoCoverageVerification.class).get();
       
        assertObject(jacocoExtension);
        assertObject(jacocoTestReport);
        assertObject(verificationReport);
    }
    
    @Test
    @Tag("integration")
    public void plugin_Jacoco_WithModuleProject() {
        final Project rootProject = ProjectBuilder.builder().withName("root").build();
        final Project project = ProjectBuilder.builder().withParent(rootProject).withName("sub-module").build();
        project.getPlugins().apply(pluginName);
        project.evaluationDependsOn(":");
        
        final JacocoPluginExtension extension = project.getExtensions().getByType(JacocoPluginExtension.class);
        final JacocoReport coverage = project.getTasks().named("jacocoTestReport", JacocoReport.class).get();
        final JacocoCoverageVerification verify = project.getTasks().named("jacocoTestCoverageVerification", JacocoCoverageVerification.class).get();
        
        assertObject(extension);
        assertObject(coverage);
        assertObject(verify);
    }
    
    @Test
    @Tag("integration")
    public void plugin_SpotBugs_WithModuleProject() {
        final Project rootProject = ProjectBuilder.builder().withName("root").build();
        final Project project = ProjectBuilder.builder()
            .withParent(rootProject)
            .withName("sub-module")
            .build();
        
        project.getPlugins().apply(pluginName);
        project.evaluationDependsOn(":");

        final SpotBugsExtension extension = project.getExtensions().getByType(SpotBugsExtension.class);
        final SpotBugsTask spotBugsMainTask = project.getTasks().named("spotbugsMain", SpotBugsTask.class).get();
        
        assertObject(extension);
        assertObject(spotBugsMainTask);
        assertEquals("4.9.6", extension.getToolVersion().get());
        assertFalse(extension.getIgnoreFailures().get());
        
        project.getTasks().withType(SpotBugsTask.class, task -> { //
            task.getReports().forEach(report ->  {
                assertTrue(report.getRequired().get());
                //noinspection AssertBetweenInconvertibleTypes
                assertEquals("fancy-hist.xsl", report.getStylesheet());
            });
        });
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"library-test", "library-tests"})
    @Tag("integration")
    public void plugin_SpotBugs_WithTestProjectName(String name) {
        final Project project = ProjectBuilder.builder().withName(name).build();
        project.getPlugins().apply(pluginName);
        project.evaluationDependsOn(":");
        
        SpotBugsExtension extension = project.getExtensions().getByType(SpotBugsExtension.class);
        SpotBugsTask spotBugsMainTask = project.getTasks().named("spotbugsMain", SpotBugsTask.class).get();
        
        assertObject(extension);
        assertObject(spotBugsMainTask);
        assertEquals("4.9.6", extension.getToolVersion().get());
        assertTrue(extension.getIgnoreFailures().get());

        project.getTasks().withType(SpotBugsTask.class, task -> { //
            task.getReports().forEach(report -> assertFalse(report.getRequired().get()));
        });
        
        project.getTasks().withType(SpotBugsTask.class, task -> { //
            task.getReports().forEach(report -> { //
                assertFalse(report.getRequired().get());
            });
        });
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
