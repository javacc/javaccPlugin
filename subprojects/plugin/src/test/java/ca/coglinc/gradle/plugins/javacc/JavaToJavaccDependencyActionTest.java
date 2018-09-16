package ca.coglinc.gradle.plugins.javacc;

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
import org.mockito.Answers;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JavaToJavaccDependencyActionTest {
    private JavaccPlugin plugin;
    private Project project;

    @Before
    public void createProject() {
        project = ProjectBuilder.builder().build();
    }

    @Before
    public void createPlugin() {
        plugin = new JavaccPlugin();
    }

    private void applyJavaccPluginToProject() {
        plugin.apply(project);
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
    public void generatedJavaFilesFromCompileJavaccAreAddedToMainJavaSourceSet() {
        applyJavaccPluginToProject();
        applyJavaPluginToProject();
        JavaToJavaccDependencyAction action = new JavaToJavaccDependencyAction();
        final File outputDirectory = new File(getClass().getResource("/javacc/testgenerated").getFile());
        CompileJavaccTask compileJavaccTask = (CompileJavaccTask) project.getTasks().findByName(CompileJavaccTask.TASK_NAME_VALUE);
        compileJavaccTask.setOutputDirectory(outputDirectory);

        action.execute(project);

        TaskCollection<JavaCompile> javaCompilationTasks = project.getTasks().withType(JavaCompile.class);
        for (JavaCompile task : javaCompilationTasks) {
            assertTrue(task.getSource().contains(new File(outputDirectory, "someSourceFile.txt")));
        }
    }

    @Test
    public void generatedJavaccFilesFromCompileJJTreeAreAddedToCompileJavaccSourceSet() {
        applyJavaccPluginToProject();
        applyJavaPluginToProject();

        JavaToJavaccDependencyAction action = new JavaToJavaccDependencyAction();
        final File inputDirectory = new File(getClass().getResource("/jjtree/input").getFile());
        final File outputDirectory = new File(getClass().getResource("/jjtree/testgenerated").getFile());
        CompileJjtreeTask compileJjtreeTask = (CompileJjtreeTask) project.getTasks().findByName(CompileJjtreeTask.TASK_NAME_VALUE);
        compileJjtreeTask.setInputDirectory(inputDirectory);
        compileJjtreeTask.setOutputDirectory(outputDirectory);

        action.execute(project);

        TaskCollection<JavaCompile> javaCompilationTasks = project.getTasks().withType(JavaCompile.class);
        for (JavaCompile task : javaCompilationTasks) {
            assertTrue(task.getSource().contains(new File(outputDirectory, "someSourceFile.jj")));
        }

        TaskCollection<CompileJavaccTask> compileJavaccsTasks = project.getTasks().withType(CompileJavaccTask.class);
        for (CompileJavaccTask task : compileJavaccsTasks) {
            assertTrue(task.getSource().contains(new File(outputDirectory, "someSourceFile.jj")));
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

    @Test
    public void compileJavaDoesNotDependOnCompileJJTreeWhenJavaccPluginNotApplied() {
        applyJavaPluginToProject();
        JavaToJavaccDependencyAction action = new JavaToJavaccDependencyAction();

        action.execute(project);

        TaskCollection<JavaCompile> javaCompilationTasks = project.getTasks().withType(JavaCompile.class);
        for (JavaCompile task : javaCompilationTasks) {
            Set<Object> dependencies = task.getDependsOn();
            assertFalse(dependencies.contains(project.getTasks().findByName(CompileJjtreeTask.TASK_NAME_VALUE)));
        }
    }

    @Test
    public void compileJavaccDependOnCompileJJTreeWhenInputDirectoryNotEmpty() {
        applyJavaPluginToProject();
        applyJavaccPluginToProject();
        JavaToJavaccDependencyAction action = new JavaToJavaccDependencyAction();

        final File inputDirectory = new File(getClass().getResource("/jjtree/input").getFile());
        CompileJjtreeTask compileJjtreeTask = (CompileJjtreeTask) project.getTasks().findByName(CompileJjtreeTask.TASK_NAME_VALUE);
        compileJjtreeTask.setInputDirectory(inputDirectory);

        action.execute(project);

        TaskCollection<CompileJavaccTask> compileJavaccTasks = project.getTasks().withType(CompileJavaccTask.class);
        for (CompileJavaccTask task : compileJavaccTasks) {
            Set<Object> dependencies = task.getDependsOn();
            assertTrue(dependencies.contains(project.getTasks().findByName(CompileJjtreeTask.TASK_NAME_VALUE)));
        }
    }

    @Test
    public void compileJavaccDoesNotDependOnCompileJJTreeWhenInputDirectoryEmpty() {
        applyJavaPluginToProject();
        applyJavaccPluginToProject();
        JavaToJavaccDependencyAction action = new JavaToJavaccDependencyAction();

        final File inputDirectory = new File(getClass().getResource("/empty").getFile());
        CompileJjtreeTask compileJjtreeTask = (CompileJjtreeTask) project.getTasks().findByName(CompileJjtreeTask.TASK_NAME_VALUE);
        compileJjtreeTask.setInputDirectory(inputDirectory);

        action.execute(project);

        TaskCollection<CompileJavaccTask> compileJavaccTasks = project.getTasks().withType(CompileJavaccTask.class);
        for (CompileJavaccTask task : compileJavaccTasks) {
            Set<Object> dependencies = task.getDependsOn();
            assertFalse(dependencies.contains(project.getTasks().findByName(CompileJjtreeTask.TASK_NAME_VALUE)));
        }
    }

    @Test
    public void noInteractionsWithProjectIfJavaPluginNotApplied() {
        project = Mockito.mock(Project.class, Answers.RETURNS_MOCKS.get());
        JavaToJavaccDependencyAction action = new JavaToJavaccDependencyAction();

        action.execute(project);

        Mockito.verify(project).getPlugins();
        Mockito.verifyNoMoreInteractions(project);
    }
}
