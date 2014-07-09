package ca.coglinc.gradle.plugins.javacc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

public class JavaToJavaccDependencyActionTest {
    private Project project;
    
    @Before
    public void createProject() {
        project = ProjectBuilder.builder().build();
    }

    private void applyJavaccPluginToProject() {
        Map<String, String> pluginNames = new HashMap<String, String>(1);
        pluginNames.put("plugin", "ca.coglinc.javacc");
        project.apply(pluginNames);
    }
    
    private void applyJavaPluginToProject() {
        Map<String, String> pluginNames = new HashMap<String, String>(1);
        pluginNames.put("plugin", "java");
        project.apply(pluginNames);
    }

    @Test
    public void compileJavaDependsOnCompileJavaccAfterExecutionWhenJavaPluginApplied() {
        applyJavaccPluginToProject();
        applyJavaPluginToProject();
        JavaToJavaccDependencyAction action = new JavaToJavaccDependencyAction();
        
        action.execute(project);
        
        TaskCollection<JavaCompile> javaCompilationTasks = project.getTasks().withType(JavaCompile.class);
        for (JavaCompile task : javaCompilationTasks) {
            Set<Object> dependencies = task.getDependsOn();
            assertTrue(dependencies.contains(project.getTasks().findByName(CompileJavaccTask.TASK_NAME_VALUE)));
        }
    }
    
    @Test
    public void generatedJavaFilesAreAddedToMainJavaSourceSet() {
        applyJavaccPluginToProject();
        applyJavaPluginToProject();
        JavaToJavaccDependencyAction action = new JavaToJavaccDependencyAction();
        final File outputDirectory = new File(getClass().getResource("/testgenerated").getFile());
        CompileJavaccTask compileJavaccTask = (CompileJavaccTask) project.getTasks().findByName(CompileJavaccTask.TASK_NAME_VALUE);
        compileJavaccTask.setOutputDirectory(outputDirectory);
        
        action.execute(project);
        
        TaskCollection<JavaCompile> javaCompilationTasks = project.getTasks().withType(JavaCompile.class);
        for (JavaCompile task : javaCompilationTasks) {
            assertFalse(task.getSource().contains(new File(outputDirectory.getAbsolutePath() + File.separator + "JavaccTestOutput.java")));
        }
    }
    
    
    @Test
    public void compileJavaDoesNotDependOnCompileJavaccWhenJavaccPluginNotApplied() {
        applyJavaPluginToProject();
        JavaToJavaccDependencyAction action = new JavaToJavaccDependencyAction();
        
        action.execute(project);
        
        TaskCollection<JavaCompile> javaCompilationTasks = project.getTasks().withType(JavaCompile.class);
        for (JavaCompile task : javaCompilationTasks) {
            Set<Object> dependencies = task.getDependsOn();
            assertFalse(dependencies.contains(project.getTasks().findByName(CompileJavaccTask.TASK_NAME_VALUE)));
        }
    }
}
