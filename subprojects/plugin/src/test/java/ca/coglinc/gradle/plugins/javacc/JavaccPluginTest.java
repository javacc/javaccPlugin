package ca.coglinc.gradle.plugins.javacc;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JavaccPluginTest {
    private Project project;

    @Before
    public void applyJavaccPluginToProject() {
        project = ProjectBuilder.builder().build();
        Map<String, String> pluginNames = new HashMap<String, String>(1);
        pluginNames.put("plugin", "ca.coglinc.javacc");

        project.apply(pluginNames);
    }

    @Test
    public void pluginAddsCompileJavaccTaskToProject() {
        final Task compileJavaccTask = project.getTasks().getByName("compileJavacc");
        Assert.assertNotNull(compileJavaccTask);
        Assert.assertTrue(compileJavaccTask instanceof CompileJavaccTask);
    }

    @Test
    public void pluginAddsCompileJJTreeTaskToProject() {
        final Task compileJJTreeTask = project.getTasks().getByName("compileJjtree");
        Assert.assertNotNull(compileJJTreeTask);
        Assert.assertTrue(compileJJTreeTask instanceof CompileJjTreeTask);
    }
}
