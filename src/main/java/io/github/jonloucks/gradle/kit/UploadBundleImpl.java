package io.github.jonloucks.gradle.kit;

import io.github.jonloucks.variants.api.Environment;
import io.github.jonloucks.variants.api.Variant;
import okhttp3.*;
import org.gradle.api.GradleException;

import java.io.File;
import java.io.IOException;

import static io.github.jonloucks.contracts.api.Checks.nullCheck;
import static io.github.jonloucks.gradle.kit.Configs.*;
import static io.github.jonloucks.gradle.kit.Internal.base64Encode;

final class UploadBundleImpl {
    
    UploadBundleImpl(Environment environment, String bundleName, File bundleFile) {
        this.environment = nullCheck(environment, "Environment must be present.");
        this.bundleName = nullCheck(bundleName, "Bundle name must be present.");
        this.bundleFile = nullCheck(bundleFile, "Bundle file must be present.");
    }
    
    void upload() {
        checkBundleFile();
        final String url = getPublisherUrl();
        final String username = getAuthorUsername();
        final String password = getAuthorPassword();
        final String encodedAuthString = base64Encode(username + ":" + password);
        final OkHttpClient client = new OkHttpClient();
        
        final RequestBody requestBody = new MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("bundle", bundleName,
                RequestBody.create(bundleFile, MediaType.parse("application/x-tar")))
            .build();
        
        final Request request = new Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", "Bearer " + encodedAuthString)
            .header("accept", "text/plain; charset=UTF-8")
            .build();
        
        if ("dry-run".equals(username)) {
            return;
        }
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new GradleException("Failure response: " + response + ".");
            }
        } catch (IOException thrown) {
            throw new GradleException("Unable connect to " + url + "." , thrown);
        }
    }
    
    private void checkBundleFile() {
        if (!bundleFile.exists()) {
            throw new GradleException("Bundle file not found at: " + bundleFile.getAbsolutePath() + ".");
        }
    }
    
    private String getPublisherUrl() {
        return requireNonEmpty(KIT_OSSRH_URL,"Publisher url must be present.");
    }
    
    private String getAuthorUsername() {
        return requireNonEmpty(KIT_OSSRH_USERNAME,"Author username must be present.");
    }
    
    private String getAuthorPassword() {
        return requireNonEmpty(KIT_OSSRH_PASSWORD,"Author password must be present.");
    }
    
    private String requireNonEmpty(Variant<String> variant, String message) {
        return environment.findVariance(variant).orElseThrow(() -> new GradleException(message));
    }

    private final Environment environment;
    private final String bundleName;
    private final File bundleFile;
}
