package io.github.jonloucks.gradle.kit.test;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.internal.DefaultGradleRunner;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Collections.singletonMap;
import static java.util.Optional.ofNullable;

final class KitGradleRunner extends DefaultGradleRunner {
    
    public KitGradleRunner withPlugins(String ... plugins) {
        this.plugins = plugins;
        return this;
    }
    
    @Override
    public KitGradleRunner withEnvironment(Map<String, String> map) {
        overrideMap.putAll(map);
        return this;
    }
    
    @Override
    public BuildResult build() {
        return withDeploy(super::build);
    }
    
    @Override
    public BuildResult buildAndFail() {
        return withDeploy(super::buildAndFail);
    }
    
    @Override
    public BuildResult run() {
        return withDeploy(super::run);
    }
    
    @Override
    public KitGradleRunner withDebug(boolean enabled) {
        if (enabled) {
            super.withDebug(true);
            return this;
        }
        throw new UnsupportedOperationException("Debug mode is required.");
    }
    
    @Override
    public KitGradleRunner withProjectDir(File dir){
        throw new UnsupportedOperationException("withProjectDir not supported.");
    }
    
    KitGradleRunner() {
        super.withDebug(true); // for code coverage, attempting to get test to run in current JDK
        withEnvironment(singletonMap("gradle.kit.log.enabled", "true")); // some tests rely on output for verification
    }
    
    private BuildResult withDeploy(Supplier<BuildResult> supplier) {
        final Map<String,String> previous = new HashMap<>();
        overrideMap.forEach((k, v) -> previous.put(k, setSystemProperty(k, v)));
        final Path projectDir = ProjectDeployer.deploy(plugins);
        super.withProjectDir(projectDir.toFile());
        try {
            return supplier.get();
        } finally {
            previous.forEach(this::setSystemProperty);
            ProjectDeployer.deleteDeploy(projectDir);
        }
    }
    
    private String setSystemProperty(String key, String value) {
        if (ofNullable(value).isEmpty()) {
            return System.clearProperty(key);
        } else {
            return System.setProperty(key, value);
        }
    }
    
    private final Map<String,String> overrideMap = new HashMap<>();
    private String[] plugins;
}
