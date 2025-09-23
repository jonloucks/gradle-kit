package io.github.jonloucks.gradle.kit;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.jetbrains.annotations.NotNull;

import static io.github.jonloucks.gradle.kit.Internal.isRootProject;
import static io.github.jonloucks.gradle.kit.Internal.log;

@SuppressWarnings("CodeBlock2Expr")
final class JacocoApplier {

    JacocoApplier(Project project) {
        this.targetProject = project;
    }
    
    void apply() {
        log("Applying jacoco plugin...");
        targetProject.getPlugins().apply("jacoco");
        
        configureJacocoPlugin();
        disableExistingJacocoReports();
        
        if (isRootProject(targetProject)) {
            registerJacocoTestReportAll();
        }
    }
    
    private void disableExistingJacocoReports() {
        targetProject.getTasks().named("jacocoTestReport", JacocoReport.class)
            .configure(disableJacocoReport());
    }
    
    private void registerJacocoTestReportAll() {
        final Project rootProject = targetProject.getRootProject();
        log("Registering " + REPORT_ALL_TASK_NAME + " ...");
        rootProject.getTasks().register(REPORT_ALL_TASK_NAME, JacocoReport.class)
            .configure(reportTask -> {
                reportTask.setGroup("Verification");
                reportTask.shouldRunAfter("test", "integrationTest", "functionalTest");
                reportTask.dependsOn("test", "integrationTest", "functionalTest");
                reportTask.reports(reports -> {
                    reports.getHtml().getRequired().set(true); // might make this an option, not needed for workflow
                    reports.getXml().getRequired().set(true);
                    reports.getCsv().getRequired().set(false);
                });
                rootProject.allprojects(getAllJacocoFiles(reportTask));
            });
        rootProject.getTasks().withType(TEST_TYPE).configureEach(testTask -> {
            testTask.finalizedBy(REPORT_ALL_TASK_NAME);
        });
        targetProject.getTasks().named("check").configure(checkTask -> {
            checkTask.dependsOn(REPORT_ALL_TASK_NAME);
        });
    }
    
    private @NotNull Action<@NotNull Project> getAllJacocoFiles(JacocoReport reportTask) {
        return project -> {
            final SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
            final SourceSet sourceSet = sourceSets.getByName("main");
            final DirectoryProperty buildDir = project.getLayout().getBuildDirectory();
      
            reportTask.getInputs().getSourceFiles().plus(sourceSet.getJava());
            reportTask.getAdditionalSourceDirs().from(sourceSet.getJava().getSrcDirs());
            reportTask.getAdditionalClassDirs().from(sourceSet.getOutput().getClassesDirs());
            reportTask.getExecutionData().from(project.fileTree(buildDir.dir("jacoco")).include("**.exec"));
        };
    }
    
    private void configureJacocoPlugin() {
        targetProject.getPlugins().withType(JacocoPlugin.class, jacocoPlugin -> {
            JacocoPluginExtension jacocoExtension = targetProject.getExtensions().getByType(JacocoPluginExtension.class);
            // Configure the toolVersion here
            jacocoExtension.setToolVersion("0.8.13");
        });
    }
    
    private static @NotNull Action<@NotNull JacocoReport> disableJacocoReport() {
        return reportTask -> {
            reportTask.setEnabled(false);
            reportTask.reports(reports -> {
                reports.getHtml().getRequired().set(false);
                reports.getXml().getRequired().set(false);
                reports.getCsv().getRequired().set(false);
            });
        };
    }
    
    private static final String REPORT_ALL_TASK_NAME = "jacocoTestReportAll";
    private static final Class<org.gradle.api.tasks.testing.Test> TEST_TYPE = org.gradle.api.tasks.testing.Test.class;
    
    private final Project targetProject;
}
