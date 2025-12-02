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
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.gradle.testing.jacoco.tasks.JacocoReportBase;
import org.gradle.testing.jacoco.tasks.rules.JacocoViolationRulesContainer;

import java.math.BigDecimal;

import static io.github.jonloucks.gradle.kit.Internal.isTestingTaskName;

@SuppressWarnings("CodeBlock2Expr")
final class JacocoApplier extends ProjectApplier {

    JacocoApplier(Project project) {
        super(project);
    }
    
    @Override
    void apply() {
        applyOnce(() -> {
            log("Applying jacoco plugin...");
            getProject().getPlugins().apply("jacoco");
            
            getProject().afterEvaluate(project -> {
                configureJacocoPlugin();
                configureExistingReports();
                configureVerificationReports();
            });
        });
    }
    
    private void configureExistingReports() {
        getProject().getTasks().named(JACOCO_TEST_REPORT, JacocoReport.class)
            .configure(configureExistingReport());
    }
    
    private void configureVerificationReports() {
        getProject().getTasks().named(JACOCO_VERIFICATION_REPORT, JacocoCoverageVerification.class)
            .configure(configureExistingVerificationReport());
    }
    
    private Action<JacocoCoverageVerification> configureExistingVerificationReport() {
        return verification -> {
            if (isRootProject()) {
                verification.violationRules(rules -> {
                    addViolationRules(rules, "LINE", "BRANCH", "CLASS", "INSTRUCTION", "METHOD");
                });
                getProject().allprojects(getAllJacocoFiles(verification));
            } else {
                verification.setEnabled(false);
            }
        };
    }
    
    private static void addViolationRules(JacocoViolationRulesContainer rules, String ... counters) {
        for (String counter : counters) {
            rules.rule(rule -> {
                rule.limit(limit -> {
                    limit.setCounter(counter);
                    limit.setValue("COVEREDRATIO");
                    limit.setMinimum(BigDecimal.valueOf(0.95));
                });
            });
        }
    }
    
    private void configureJacocoPlugin() {
        getProject().getPlugins().withType(JacocoPlugin.class, jacocoPlugin -> {
            JacocoPluginExtension jacocoExtension = getProject().getExtensions().getByType(JacocoPluginExtension.class);
            jacocoExtension.setToolVersion("0.8.13");
        });
    }
    
    private Action<JacocoReport> configureExistingReport() {
        
        return reportTask -> {
            final TaskCollection<Test> testingTasks = getTestingTasks(getProject());
            reportTask.shouldRunAfter(testingTasks);
            reportTask.dependsOn(testingTasks);
            reportTask.reports(reports -> {
                reports.getHtml().getRequired().set(isRootProject());
                reports.getXml().getRequired().set(true);
                reports.getCsv().getRequired().set(isRootProject());
            });
            if (isRootProject()) {
                getProject().allprojects(getAllJacocoFiles(reportTask));
            }
        };
    }

    private Action<Project> getAllJacocoFiles(JacocoReportBase rootReport) {
        return project -> {
            if (isTestProject(project)) {
                return;
            }
            final TaskCollection<Test> testingTasks = getTestingTasks(project);
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
    
    private static TaskCollection<Test> getTestingTasks(Project project) {
        return project.getTasks().withType(Test.class).matching(t -> {
            return isTestingTaskName(t.getName());
        });
    }
    
    private static final String JACOCO_TEST_REPORT = "jacocoTestReport";
    private static final String JACOCO_VERIFICATION_REPORT = "jacocoTestCoverageVerification";
}
