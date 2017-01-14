package ca.coglinc.gradle.plugins.javacc;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class JavaccPluginTest {
    private JavaccPlugin plugin;
    private Project project;

    @Before
    public void applyJavaccPluginToProject() {
        plugin = new JavaccPlugin();
        project = ProjectBuilder.builder().build();

        plugin.apply(project);
    }

    @Test
    public void pluginAddsCompileJavaccTaskToProject() {
        final Task compileJavaccTask = project.getTasks().getByName("compileJavacc");

        assertNotNull(compileJavaccTask);
        assertTrue(compileJavaccTask instanceof CompileJavaccTask);
    }

    @Test
    public void pluginAddsCompileJJTreeTaskToProject() {
        final Task compileJJTreeTask = project.getTasks().getByName("compileJjtree");

        assertNotNull(compileJJTreeTask);
        assertTrue(compileJJTreeTask instanceof CompileJjTreeTask);
    }

    @Test
    public void pluginAddsCompileJjdocTaskToProject() {
        final Task compileJjdocTask = project.getTasks().getByName("jjdoc");

        assertNotNull(compileJjdocTask);
        assertTrue(compileJjdocTask instanceof CompileJjdocTask);
    }

    @Test
    public void pluginDefinesDefaultDependencyOnJavacc() {
        Configuration javaccDependencyConfiguration = project.getConfigurations().getByName("javacc");

        assertNotNull(javaccDependencyConfiguration);
    }
}
