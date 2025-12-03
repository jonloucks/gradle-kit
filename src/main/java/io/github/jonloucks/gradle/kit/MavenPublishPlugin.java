package io.github.jonloucks.gradle.kit;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.tasks.bundling.Tar;
import org.gradle.api.tasks.bundling.Zip;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static io.github.jonloucks.gradle.kit.Configs.*;
import static io.github.jonloucks.gradle.kit.Internal.*;

/**
 * Extension of the Gradle 'maven-publish' plugin
 */
public final class MavenPublishPlugin implements Plugin<Project> {
    
    /**
     * Invoked via reflection by Gradle
     */
    @Override
    public void apply(Project project) {
        new Applier(project).apply();
    }
    
    @SuppressWarnings("CodeBlock2Expr")
    private static final class Applier extends ProjectApplier {
        private Applier(Project project) {
            super(project);
        }
        
        @Override
        void apply() {
            applyMavenPublishPlugin();
            
            getProject().afterEvaluate(x -> {
                if (isRootProject()) {
                    registerCreatePublisherBundle();
                    registerUploadPublisherBundle();
                }
                
                createStagingRepository();
                configureChecksums();
            });
        }
        
        @SuppressWarnings("Convert2Lambda") // gradle does not like lambda here
        private void configureChecksums() {
            getProject().getTasks().withType(Zip.class).configureEach(new Action<>() {
                @Override
                public void execute(Zip zip) {
                    zip.doLast(new Action<>() {
                        @Override
                        public void execute(Task task) {
                            createChecksums(zip.getArchiveFile().get().getAsFile());
                        }
                    });
                }
            });
        }
        
        private void createStagingRepository() {
            log("Creating staging repository...");
            getProject().getExtensions().configure(PublishingExtension.class, extension -> {
                extension.repositories(r -> {
                    r.maven(maven -> {
                        maven.setName("LocalMavenWithChecksums");
                        maven.setUrl(getProject().getLayout().getBuildDirectory().dir("staging-deploy"));
                    });
                });
            });
        }
        
        private void applyMavenPublishPlugin() {
            log("Applying maven-publish plugin...");
            getProject().getPlugins().apply("maven-publish");
        }
        
        private void registerCreatePublisherBundle() {
            log("Registering " + CREATE_BUNDLE_TASK_NAME + " ...");
            getProject().getTasks().register(CREATE_BUNDLE_TASK_NAME, Tar.class).configure(tar -> {
                tar.getArchiveBaseName().set(getProject().getGroup().toString());
                tar.getArchiveVersion().set(getProject().getVersion().toString());
                tar.getDestinationDirectory().set(getProject().getLayout().getBuildDirectory().dir("distributions"));
                getProject().allprojects(p -> tar.from(p.getLayout().getBuildDirectory().dir("staging-deploy")));
            });
        }
        
        private static String createTimestamp() {
            return ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX"));
        }
        
        private void registerUploadPublisherBundle() {
            log("Registering " + UPLOAD_BUNDLE_TASK_NAME + " ...");
            
            getProject().getTasks().register(UPLOAD_BUNDLE_TASK_NAME).configure(task -> {
                task.doLast(action -> {
                    uploadBundle(getEnvironment(), getBundleName(), getBundleFile());
                });
            });
        }
        
        private File getBundleFile() {
            return getProject().file("build/distributions/" + getProject().getGroup() + "-" + getProject().getVersion() + ".tar");
        }
        
        private String getBundleName() {
            final String workflowName = requireConfig(KIT_PROJECT_WORKFLOW);
            return getProject().getGroup() + "-" + getProject().getVersion() + " by " + workflowName + " @ " + createTimestamp();
        }

        private static final String CREATE_BUNDLE_TASK_NAME = "createPublisherBundle";
        private static final String UPLOAD_BUNDLE_TASK_NAME = "uploadPublisherBundle";
    }
}
