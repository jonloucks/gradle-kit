package io.github.jonloucks.gradle.kit;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.plugins.signing.SigningExtension;
import org.jetbrains.annotations.NotNull;

import static io.github.jonloucks.gradle.kit.Internal.*;
import static java.util.Optional.ofNullable;

/**
 * Extension of the Gradle 'signing' plugin
 */
public final class SigningPlugin implements Plugin<@NotNull Project> {
    
    /**
     * Invoked via reflection by Gradle
     */
    public SigningPlugin() {
    
    }
    
    public void apply(Project project) {
        new Apply(project).apply();
    }
    
    @SuppressWarnings("CodeBlock2Expr")
    private static final class Apply {
        private final Project project;
        
        private Apply(Project project) {
            this.project = project;
        }
        
        private void apply() {
            applySigningPlugin();
            configureSigning();
        }
        
        private void configureSigning() {
            final String secretKey = getGpgSecretKey();
            final String secretPassword = getGpgSecretKeyPassword();
            if (ofNullable(secretKey).isPresent() && ofNullable(secretPassword).isPresent()) {
                log("Configuring signing keys...");
                project.getExtensions().configure(SigningExtension.class, signing -> {
                    signing.useInMemoryPgpKeys(secretKey, secretPassword);
                });
            }
        }
        
        private void applySigningPlugin() {
           log("Applying signing plugin...");
            project.getPlugins().apply("signing");
        }
        
        private String getGpgSecretKey() {
            final String secretKey = getConfig(project, "OSSRH_GPG_SECRET_KEY");
            if (ofNullable(secretKey).isPresent()) {
                if (secretKey.startsWith("-")) {
                    return secretKey;
                }
                try {
                    return base64Decode(secretKey);
                } catch (IllegalArgumentException thrown) {
                    throw new GradleException("Invalid gpg secret key: " + secretKey, thrown);
                }
            }
            return null;
        }
        
        private String getGpgSecretKeyPassword() {
            return getConfig(project, "OSSRH_GPG_SECRET_KEY_PASSWORD");
        }
    }
}