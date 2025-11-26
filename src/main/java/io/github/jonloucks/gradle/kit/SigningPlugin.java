package io.github.jonloucks.gradle.kit;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.plugins.signing.SigningExtension;

import static io.github.jonloucks.gradle.kit.Configs.*;
import static java.util.Optional.ofNullable;

/**
 * Extension of the Gradle 'signing' plugin
 */
public final class SigningPlugin implements Plugin<Project> {
    
    /**
     * Invoked via reflection by Gradle
     */
    public SigningPlugin() {
    
    }
    
    public void apply(Project project) {
        new Apply(project).apply();
    }
    
    @SuppressWarnings("CodeBlock2Expr")
    private static final class Apply extends ProjectApplier {
        
        private Apply(Project project) {
            super(project);
        }
        
        @Override
        void apply() {
            applySigningPlugin();
            getProject().afterEvaluate(x -> {
                configureSigning();
            });
        }
        
        private void configureSigning() {
            final String secretKey = getGpgSecretKey();
            final String secretPassword = getGpgSecretKeyPassword();
            if (ofNullable(secretKey).isPresent() && ofNullable(secretPassword).isPresent()) {
                log("Configuring signing keys...");
                getProject().getExtensions().configure(SigningExtension.class, signing -> {
                    signing.useInMemoryPgpKeys(secretKey, secretPassword);
                });
            }
        }
        
        private void applySigningPlugin() {
           log("Applying signing plugin...");
            getProject().getPlugins().apply("signing");
        }
        
        private String getGpgSecretKey() {
            return getConfig(KIT_GPG_SECRET_KEY).orElse(null);
        }
        
        private String getGpgSecretKeyPassword() {
            return getConfig(KIT_GPG_SECRET_KEY_PASSWORD).orElse(null);
        }
    }
}