package ca.coglinc.gradle.plugins.javacc;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;

public class JavaToJavaccDependencyAction implements Action<Project> {

    @Override
    public void execute(Project project) {
        if (!project.getPlugins().hasPlugin("java")) {
            return;
        }

        configureCompileJJTreeTask(project);
        configureCompileJavaccTask(project);
    }

    private void configureCompileJJTreeTask(Project project) {
        CompileJJTreeTask compileJJTreeTask = (CompileJJTreeTask) project.getTasks().findByName(CompileJJTreeTask.TASK_NAME_VALUE);
        if (compileJJTreeTask == null) {
            return;
        }

        if (!compileJJTreeTask.getSource().isEmpty()) {
            addJJTreeDependencyToJavaccCompileTask(project.getTasks().withType(JavaCompile.class),
                project.getTasks().withType(CompileJavaccTask.class), compileJJTreeTask);
        }
    }

    private void configureCompileJavaccTask(Project project) {
        CompileJavaccTask compileJavaccTask = (CompileJavaccTask) project.getTasks().findByName(CompileJavaccTask.TASK_NAME_VALUE);
        if (compileJavaccTask != null) {
            addJavaccDependencyToJavaCompileTask(project.getTasks().withType(JavaCompile.class), compileJavaccTask);
        }
    }

    private void addJavaccDependencyToJavaCompileTask(TaskCollection<JavaCompile> javaCompilationTasks, CompileJavaccTask compileJavaccTask) {
        for (JavaCompile task : javaCompilationTasks) {
            task.dependsOn(compileJavaccTask);
            task.source(compileJavaccTask.getOutputDirectory());
        }
    }

    private void addJJTreeDependencyToJavaccCompileTask(TaskCollection<JavaCompile> javaCompilationTasks,
        TaskCollection<CompileJavaccTask> javaccCompilationTasks, CompileJJTreeTask compileJJTreeTask) {
        for (JavaCompile task : javaCompilationTasks) {
            task.dependsOn(compileJJTreeTask);
            task.source(compileJJTreeTask.getOutputDirectory());
        }

        for (CompileJavaccTask task : javaccCompilationTasks) {
            task.dependsOn(compileJJTreeTask);
            task.source(compileJJTreeTask.getOutputDirectory());
        }
    }
}
