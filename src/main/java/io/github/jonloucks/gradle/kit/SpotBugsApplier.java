package io.github.jonloucks.gradle.kit;

import com.github.spotbugs.snom.SpotBugsExtension;
import com.github.spotbugs.snom.SpotBugsPlugin;
import com.github.spotbugs.snom.SpotBugsTask;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.jetbrains.annotations.NotNull;

import static io.github.jonloucks.gradle.kit.Internal.*;

final class SpotBugsApplier {
    
    void apply() {
        log("Applying spotbugs plugin...");
        targetProject.getPlugins().apply("com.github.spotbugs");
        
        targetProject.afterEvaluate(project -> {
            log("After Evaluate SpotBugs Plugin...");
            configureSpotbugsPlugin();
            configureSpotbugsReports();
        });
    }
    
    private void configureSpotbugsReports() {
        targetProject.getTasks().named("spotbugsMain", SpotBugsTask.class)
            .configure(configureSpotbugsReport());
    }
    
    private @NotNull Action<@NotNull SpotBugsTask> configureSpotbugsReport() {
        final boolean isTestProject = isTestProject(targetProject);
        final DirectoryProperty buildDir = targetProject.getLayout().getBuildDirectory();
        
        return reportTask -> {
            if (isTestProject) {
                reportTask.setEnabled(false);
            } else {
                reportTask.setEnabled(true);
                reportTask.getReports().forEach(report -> {
                    report.getRequired().set(true);
                    report.getOutputLocation().set(buildDir.file("reports/spotbugs/main/spotbugs.html"));
                    report.setStylesheet("fancy-hist.xsl");
                });
            }
        };
    }
    
    SpotBugsApplier(Project project) {
        this.targetProject = project;
    }
 
    private void configureSpotbugsPlugin() {
        final boolean isTestProject = isTestProject(targetProject);
        final DirectoryProperty buildDir = targetProject.getLayout().getBuildDirectory();
        targetProject.getPlugins().withType(SpotBugsPlugin.class, jacocoPlugin -> {
            final SpotBugsExtension extension = targetProject.getExtensions().getByType(SpotBugsExtension.class);
            extension.getToolVersion().set("4.9.6");
            extension.getIgnoreFailures().set(isTestProject);
            extension.getReportsDir().set(buildDir.dir("reports/spotbugs"));
        });
    }
    
    private final Project targetProject;
}
