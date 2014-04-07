package ca.coglinc.gradle.plugins.javacc;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class JavaccPluginTest {
    private Project project;

    @Before
    public void applyJavaccPluginToProject() {
        project = ProjectBuilder.builder().build();
        Map<String, String> pluginNames = new HashMap<String, String>(1);
        pluginNames.put("plugin", "javacc");
        
        project.apply(pluginNames);
    }

    @Test
    public void pluginAddsCompileJavaccTaskToProject() {
        final Task compileJavaccTask = project.getTasks().getByName("compileJavacc");
        assertNotNull(compileJavaccTask);
        assertTrue(compileJavaccTask instanceof CompileJavaccTask);
    }
}
