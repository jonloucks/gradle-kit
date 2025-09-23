package io.github.jonloucks.gradle.kit.test;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.github.jonloucks.gradle.kit.test.Internal.JAVA_LIBRARY_KIT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("functionalTest")
public class JavaLibraryPluginFunctionalTest {
    
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
        final Path projectFolder = Paths.get("build/functionalTest");
        Files.createDirectories(projectFolder);
        writeString(projectFolder.resolve( "settings.gradle"), "");
        writeString(projectFolder.resolve("build.gradle"),
            "plugins {" +
                "  id('" + JAVA_LIBRARY_KIT + "')" +
                "}");

        // Run the build
        BuildResult result = GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("tasks")
            .withProjectDir(projectFolder.toFile())
            .build();

        final String output = result.getOutput();

        assertNotNull(output);
        // Verify the result
        assertThat(output, containsString("Applying java-library plugin..."));
        assertThat(output, containsString("Applying jacoco plugin..."));
        assertThat(output, not(containsString("Applying maven-publish plugin...")));
        assertThat(output, not(containsString("Applying signing plugin...")));
    }
    
    @Test
    public void run_IntegrationTest_Works() throws Throwable {
        final Path projectFolder = Paths.get("build/functionalTest");
        Files.createDirectories(projectFolder);
        final Path sourceFolder = projectFolder.resolve("src/main/java");
        final Path testFolder = projectFolder.resolve("src/test/java");
        Files.createDirectories(testFolder);
        Files.createDirectories(sourceFolder);
        
        writeString(sourceFolder.resolve("SomeImpl.java"),
            "public class SomeImpl {\n" +
            "    public SomeImpl() {}\n" +
            "}");
        
        writeString(testFolder.resolve("SomeTest.java"),
            "import org.junit.jupiter.api.Tag;\n" +
            "import org.junit.jupiter.api.Test;\n" +
            "\n" +
            "public class SomeTest {\n" +
            "    @Test\n" +
            "    public void normalTest() throws Exception {\n" +
            "       new SomeImpl(); \n" +
            "    }\n" +
            "    @Tag(\"integration\")\n" +
            "    @Test\n" +
            "    public void integrationTest1() throws Exception {\n" +
            "        \n" +
            "    }\n" +
            "    @Tag(\"functional\")\n" +
            "    @Test\n" +
            "    public void functionalTest1() throws Exception {\n" +
            "        \n" +
            "    }\n" +
            "    @Tag(\"unstable\")\n" +
            "    @Test\n" +
            "    public void unstableTest1() throws Exception {\n" +
            "        throw new RuntimeException(\"Should have been filtered\");\n" +
            "    }\n" +
            "    @Tag(\"slow\")\n" +
            "    @Test\n" +
            "    public void slowTest1() throws Exception {\n" +
            "        \n" +
            "    }\n" +
            "}");
        
        writeString(projectFolder.resolve( "settings.gradle"), "");
        writeString(projectFolder.resolve("build.gradle"),
            "plugins { \n" +
                "  id '" + JAVA_LIBRARY_KIT + "' \n" +
                "} \n" +
                "dependencies {\n" +
                "    testImplementation platform('org.junit:junit-bom:5.10.0')\n" +
                "    testImplementation 'org.junit.jupiter:junit-jupiter'\n" +
                "    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'\n" +
                "} \n" +
                "test {\n" +
                "    useJUnitPlatform()\n" +
                "}\n" +
                "repositories { \n" +
                "    mavenCentral() \n" +
                "}\n"
        );
        
        // Run the build
        BuildResult result = GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("check")
            .withProjectDir(projectFolder.toFile())
            .withDebug(true)
            .build();
        
        final String output = result.getOutput();
        
        assertNotNull(output);
        assertThat(output, containsString("Applying java-library plugin..."));
        assertThat(output, not(containsString("Applying maven-publish plugin...")));
        assertThat(output, not(containsString("Applying signing plugin...")));
    }
    
    private void writeString(Path path, String string) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            writer.write(string);
        }
    }
}
