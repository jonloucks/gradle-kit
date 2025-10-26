package io.github.jonloucks.gradle.kit;

import okhttp3.*;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.tasks.bundling.Tar;
import org.gradle.api.tasks.bundling.Zip;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static io.github.jonloucks.gradle.kit.Configs.*;
import static io.github.jonloucks.gradle.kit.Internal.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;

/**
 * Extension of the Gradle 'maven-publish' plugin
 */
public final class MavenPublishPlugin implements Plugin<@NotNull Project> {
    
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
        
        private void configureChecksums() {
            getProject().getTasks().withType(Zip.class).configureEach(zip -> {
                zip.doLast(task -> {
                    final File file = zip.getArchiveFile().get().getAsFile();
                    createMD5Checksum(file);
                    createSHA1Checksum(file);
                });
            });
        }
        
        private void createSHA1Checksum(File file) {
            generateChecksum(file, new File(file.getAbsolutePath() + ".sha1"), "SHA1");
        }
        
        private void createMD5Checksum(File file) {
            generateChecksum(file, new File(file.getAbsolutePath() + ".md5"), "MD5");
        }
        
        public static void generateChecksum(File inputFile, File outputFile, String algorithm) {
            try {
                writeDigestBytes(outputFile, createDigestBytes(inputFile, algorithm));
            } catch (Exception thrown) {
                throw new GradleException(thrown.getMessage(), thrown);
            }
        }
        
        private static void writeDigestBytes(File outputFile, byte[] digestBytes) throws Exception {
            final StringBuilder stringBuilder = new StringBuilder();
            for (byte b : digestBytes) {
                stringBuilder.append(String.format("%02x", b));
            }
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(stringBuilder.toString().getBytes(UTF_8));
            }
        }
        
        private static byte[] createDigestBytes(File inputFile, String algorithm) throws Exception {
            final MessageDigest digest = MessageDigest.getInstance(algorithm);
            
            try (FileInputStream inputStream = new FileInputStream(inputFile)) {
                final byte[] buffer = new byte[1024];
                int bytesCount;
                while ((bytesCount = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesCount);
                }
                return digest.digest();
            }
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
        
        private String getWorkflowName() {
            return getConfig(KIT_PROJECT_WORKFLOW).orElse("unknown");
        }
        
        private String getPublishUsername() {
            return getConfig(KIT_OSSRH_USERNAME).orElse(null);
        }
        
        private String getPublishPassword() {
            return getConfig(KIT_OSSRH_PASSWORD).orElse(null);
        }
        
        private void registerUploadPublisherBundle() {
            log("Registering " + UPLOAD_BUNDLE_TASK_NAME + " ...");
            
            getProject().getTasks().register(UPLOAD_BUNDLE_TASK_NAME).configure(task -> {
                task.doLast(action -> {
                    final String apiUrl = "https://central.sonatype.com/api/v1/publisher/upload?publishingType=USER_MANAGED";
                    final String username = getPublishUsername();
                    final String password = getPublishPassword();
                    
                    if (ofNullable(username).isEmpty() || ofNullable(password).isEmpty()) {
                        throw new GradleException("Publisher environment variables must be set.");
                    }
                    
                    final String bundleName = getBundleName();
                    final File bundleFile = getBundleFile();
                    final String encodedAuthString = base64Encode(username + ":" + password);
                    
                    if (!bundleFile.exists()) {
                        throw new GradleException("Bundle file not found at: " + bundleFile.getAbsolutePath());
                    }
                    
                    final OkHttpClient client = new OkHttpClient();
                    
                    final RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("bundle", bundleName,
                            RequestBody.create(bundleFile, MediaType.parse("application/x-tar")))
                        .build();
                    
                    final Request request = new Request.Builder()
                        .url(apiUrl)
                        .post(requestBody)
                        .header("Authorization", "Bearer " + encodedAuthString)
                        .header("accept", "text/plain; charset=UTF-8")
                        .build();
                    
                    try (Response response = client.newCall(request).execute()) {
                        if (!response.isSuccessful()) {
                            throw new GradleException("Unexpected code " + response);
                        }
                    } catch (IOException thrown) {
                        throw new GradleException(thrown.getMessage(), thrown);
                    }
                });
            });
        }
        
        private File getBundleFile() {
            return getProject().file("build/distributions/" + getProject().getGroup() + "-" + getProject().getVersion() + ".tar");
        }
        
        private String getBundleName() {
            return getProject().getGroup() + "-" + getProject().getVersion() + " by " + getWorkflowName() + " @ " + createTimestamp();
        }

        private static final String CREATE_BUNDLE_TASK_NAME = "createPublisherBundle";
        private static final String UPLOAD_BUNDLE_TASK_NAME = "uploadPublisherBundle";
    }
}
