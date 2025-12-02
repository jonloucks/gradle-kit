package io.github.jonloucks.gradle.kit;

import io.github.jonloucks.variants.api.Variant;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.UUID;

import static io.github.jonloucks.contracts.test.Tools.assertThrown;
import static io.github.jonloucks.variants.api.GlobalVariants.createVariant;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public final class ProjectApplierTests {
    @ParameterizedTest
    @ValueSource(strings = {"-test", "-tests"})
    public void applyTestProject(String projectName) {
        final Project project = ProjectBuilder.builder().withName(projectName).build();
        final ProjectApplier projectApplier = new ProjectApplier(project) {
            @Override
            void apply() {
            }
        };
        assertTrue(projectApplier.isTestProject(), "Should be a test project.");
        assertNotNull(projectApplier.getEnvironment(), "Environment should be present.");
        assertTrue(projectApplier.isRootProject(), "Should be a root project.");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"-api", "-impl"})
    public void applyOtherProject(String projectName) {
        final Project project = ProjectBuilder.builder().withName(projectName).build();
        final ProjectApplier projectApplier = new ProjectApplier(project) {
            @Override
            void apply() {
            }
        };
   
        assertFalse(projectApplier.isTestProject(), "Should be a test project.");
        assertNotNull(projectApplier.getEnvironment(), "Environment should be present.");
        assertTrue(projectApplier.isRootProject(), "Should be a root project.");
    }
    
    @Test
    public void projectApplier_applyOnce_WithSuccess_Works(@Mock Runnable runnable) {
        final Project project = ProjectBuilder.builder().build();
        final ProjectApplier projectApplier = new ProjectApplier(project) {
            @Override
            void apply() {
                applyOnce(runnable);
            }
        };
        
        projectApplier.apply();
        projectApplier.apply();
        
        verify(runnable, times(1)).run();
    }
    
    @Test
    public void projectApplier_applyOnce_WithException_RetryWorks() {
        final RuntimeException error = new IllegalStateException("Problem.");
        final Runnable runnable = () -> { throw error; };
        final Project project = ProjectBuilder.builder().build();
        final ProjectApplier projectApplier = new ProjectApplier(project) {
            @Override
            void apply() {
                applyOnce(runnable);
            }
        };
        
        assertThrown(RuntimeException.class, projectApplier::apply, "Problem.");
        assertThrown(RuntimeException.class, projectApplier::apply, "Problem.");
    }
    
    @Test
    public void projectApplier_environment() {
        final Project project = ProjectBuilder.builder().build();
        final ProjectApplier projectApplier = new ProjectApplier(project) {
            @Override
            void apply() {
            }
        };
        final String uniqueString = UUID.randomUUID().toString();
        
        project.getExtensions().getExtraProperties().set(uniqueString, uniqueString);
    
        final Variant<String> variant = createVariant((b, p) -> b //
            .name("Test variant " + uniqueString) //
            .keys( uniqueString) //
            .of(p.ofString()) //
            .fallback(() -> "unknown")
        );

        assertEquals(uniqueString, projectApplier.getEnvironment().getVariance(variant), "Environment should be equal.");
    }
}
