package io.github.jonloucks.gradle.kit.test;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

final class ProjectDeployer {
    
    static Path deploy(String ... plugins) throws Exception {
        final Path projectPath = pristineProjectPath();
        
        deploySettingsDotGradle(projectPath);
        deployBuildDotGradle(projectPath, plugins);
        deployJavaSource(projectPath);
        deployJavaTest(projectPath);
        
        return projectPath;
    }
    
    private static Path pristineProjectPath() throws IOException {
        final Path projectPath = Paths.get("build/functionalTest");
        Files.createDirectories(projectPath);
        return projectPath;
    }
    
    private static void deploySettingsDotGradle(Path projectPath) throws Exception {
        writeString(projectPath.resolve( "settings.gradle"),
            "pluginManagement {\n" +
            "    repositories {\n" +
                "        mavenLocal()\n" +
                "        gradlePluginPortal()\n" +
            "    }\n" +
            "}");
    }
    
    private static void deployBuildDotGradle(Path projectPath, String[] plugins) throws Exception {
        writeString(projectPath.resolve("build.gradle"),
            "buildscript {\n" +
                "    repositories {\n" +
                "        mavenLocal()\n" +
                "        mavenCentral()\n" +
                "        gradlePluginPortal()\n" +
                "    }\n" +
                "}\n" +
                "plugins { \n" +
                 formatPlugins(plugins) +
                "} \n" +
                "group = 'io.github.jonloucks.gradle.kit.test'\n" +
                "version = '0.0.0'\n" +
                "dependencies {\n" +
                "    testImplementation platform('org.junit:junit-bom:5.10.0')\n" +
                "    testImplementation 'org.junit.jupiter:junit-jupiter'\n" +
                "    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'\n" +
                "} \n" +
                "test {\n" +
                "    useJUnitPlatform()\n" +
                "}\n" +
                formatPublishing(plugins) +
                "repositories { \n" +
                "    mavenCentral() \n" +
                "}\n"
        );
    }
    
    private static void deployJavaSource(Path projectPath) throws Exception {
        final Path sourceFolder = projectPath.resolve("src/main/java");
        Files.createDirectories(sourceFolder);
        writeString(sourceFolder.resolve("SomeImpl.java"),
            "public class SomeImpl {\n" +
                "    public SomeImpl() {}\n" +
                "}");
        
    }
    
    private static void deployJavaTest(Path projectPath) throws Exception {
        final Path testFolder = projectPath.resolve("src/test/java");
        Files.createDirectories(testFolder);
        
        writeString(testFolder.resolve("SomeTest.java"),
            "import org.junit.jupiter.api.*;\n" +
                "\n" +
                "public class SomeTest {\n" +
                "    @Test\n" +
                "    public void normalTest() throws Exception {\n" +
                "       final SomeImpl someImpl = new SomeImpl(); \n" +
                "       if(null == someImpl.toString()) { \n" +
                "           Assertions.fail(\"Oh my.\");\n" +
                "       }\n" +
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
    }
    
    private static boolean hasPublishing(String[] plugins) {
        for (String plugin : plugins) {
            if (plugin.contains("maven-publish")) {
                return true;
            }
        }
        return false;
    }
    
    private static String formatPublishing(String[] plugins) {
        if (hasPublishing(plugins)) {
            return "publishing {\n" +
                "    publications {\n" +
                "        mavenJava(MavenPublication) {\n" +
                "            from components.java\n" +
                "            versionMapping {\n" +
                "                usage('java-api') {\n" +
                "                    fromResolutionOf('runtimeClasspath')\n" +
                "                }\n" +
                "                usage('java-runtime') {\n" +
                "                    fromResolutionResult()\n" +
                "                }\n" +
                "            }\n" +
                "            pom {\n" +
                "                name = 'Functional Test'\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
        } else {
            return "";
        }
    }
    
    private static String formatPlugins(String... plugins) {
        final StringBuilder builder = new StringBuilder();
        
        for (final String plugin : plugins) {
            builder.append("  id '").append( plugin ).append("' \n");
        }
        
        return builder.toString();
    }
    
    private static void writeString(Path path, String string) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            writer.write(string);
        }
    }
}
