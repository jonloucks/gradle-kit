package io.github.jonloucks.gradle.kit;

import com.github.spotbugs.snom.SpotBugsExtension;
import com.github.spotbugs.snom.SpotBugsTask;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;

final class SpotBugsApplier extends ProjectApplier {
    
    void apply() {
        applyOnce(() -> {
            log("Applying spotbugs plugin...");
            getProject().getPlugins().apply("com.github.spotbugs");
            
            getProject().afterEvaluate(project -> {
                log("After Evaluate SpotBugs Plugin...");
                configureSpotbugsPlugin();
                configureSpotbugsReports();
            });
        });
    }
    
    SpotBugsApplier(Project project) {
        super(project);
    }
    
    private void configureSpotbugsReports() {
        getProject().getTasks().withType(SpotBugsTask.class).configureEach(configureSpotbugsReport());
    }
    
    private Action<SpotBugsTask> configureSpotbugsReport() {
        final DirectoryProperty buildDir = getProject().getLayout().getBuildDirectory();
        
        return reportTask -> {
            if (isTestProject()) {
                reportTask.setEnabled(false);
            } else {
                reportTask.setEnabled(true);
                reportTask.getReports().configureEach(report -> {
                    report.getRequired().set(true);
                    report.getOutputLocation().set(buildDir.file("reports/spotbugs/main/spotbugs.html"));
                    report.setStylesheet("fancy-hist.xsl");
                });
            }
        };
    }

    private void configureSpotbugsPlugin() {
        final DirectoryProperty buildDir = getProject().getLayout().getBuildDirectory();
        final SpotBugsExtension extension = getProject().getExtensions().getByType(SpotBugsExtension.class);
        extension.getToolVersion().set("4.9.6");
        extension.getIgnoreFailures().set(isTestProject());
        extension.getReportsDir().set(buildDir.dir("reports/spotbugs"));
    }
}
