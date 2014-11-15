package ca.coglinc.gradle.plugins.javacc;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;

public class JavaToJavaccDependencyAction implements Action<Project> {

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

        if (compileJJTreeTask.getSource().isEmpty()) {
            project.getTasks().remove(compileJJTreeTask);
        } else {
            addJJTreeDependencyToJavaccCompileTask(project.getTasks().withType(CompileJavaccTask.class), compileJJTreeTask);
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

    private void addJJTreeDependencyToJavaccCompileTask(TaskCollection<CompileJavaccTask> javaccCompilationTasks, CompileJJTreeTask compileJJTreeTask) {
        for (CompileJavaccTask task : javaccCompilationTasks) {
            task.dependsOn(compileJJTreeTask);

            if (!compileJJTreeTask.getSource().isEmpty())
                task.setInputDirectory(compileJJTreeTask.getOutputDirectory());
        }
    }
}
