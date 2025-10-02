package io.github.jonloucks.gradle.kit;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.testing.Test;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.jetbrains.annotations.NotNull;

import static io.github.jonloucks.gradle.kit.Internal.isRootProject;
import static io.github.jonloucks.gradle.kit.Internal.log;

final class JacocoApplier {

    JacocoApplier(Project project) {
        this.targetProject = project;
    }
    
    void apply() {
        log("Applying jacoco plugin...");
        targetProject.getPlugins().apply("jacoco");
        
        configureJacocoPlugin();
        configureExistingReports();
    }
    
    private void configureExistingReports() {
        targetProject.getTasks().named(JACOCO_TEST_REPORT, JacocoReport.class)
            .configure(configureExistingReport());
    }
    
    private void configureJacocoPlugin() {
        targetProject.getPlugins().withType(JacocoPlugin.class, jacocoPlugin -> {
            JacocoPluginExtension jacocoExtension = targetProject.getExtensions().getByType(JacocoPluginExtension.class);
            jacocoExtension.setToolVersion("0.8.13");
        });
    }
    
    private @NotNull Action<@NotNull JacocoReport> configureExistingReport() {
        final boolean isRootProject = isRootProject(targetProject);
        
        return reportTask -> {
            final TaskCollection<@NotNull Test> testingTasks = getTestingTasks(targetProject);
            reportTask.shouldRunAfter(testingTasks);
            reportTask.dependsOn(testingTasks);
            reportTask.reports(reports -> {
                reports.getHtml().getRequired().set(isRootProject);
                reports.getXml().getRequired().set(true);
                reports.getCsv().getRequired().set(isRootProject);
            });
            if (isRootProject) {
                targetProject.allprojects(getAllJacocoFiles(reportTask));
            }
        };
    }
    
    private @NotNull Action<@NotNull Project> getAllJacocoFiles(JacocoReport rootReport) {
        return project -> {
            final TaskCollection<@NotNull Test> testingTasks = getTestingTasks(project);
            rootReport.shouldRunAfter(testingTasks);
            rootReport.dependsOn(testingTasks);
            
            final SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
            final SourceSet sourceSet = sourceSets.getByName("main");
            final DirectoryProperty buildDir = project.getLayout().getBuildDirectory();
            rootReport.getAdditionalSourceDirs().from(sourceSet.getJava().getSrcDirs());
            rootReport.getAdditionalClassDirs().from(sourceSet.getOutput().getClassesDirs());
            rootReport.getExecutionData().from(project.fileTree(buildDir.dir("jacoco")).include("**.exec"));
        };
    }
    
    private static TaskCollection<@NotNull Test> getTestingTasks(Project project) {
        return project.getTasks().withType(Test.class).matching(t -> {
            switch (t.getName()) {
                case "test":
                case "integrationTest":
                case "functionalTest":
                    return true;
                default:
                    return false;
            }
        });
    }
    
    private static final String JACOCO_TEST_REPORT = "jacocoTestReport";
    
    private final Project targetProject;
}
