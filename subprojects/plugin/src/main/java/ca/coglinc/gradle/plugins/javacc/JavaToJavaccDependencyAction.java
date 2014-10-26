package ca.coglinc.gradle.plugins.javacc;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;

public class JavaToJavaccDependencyAction implements Action<Project> {

    public void execute(Project project) {
        if (project.getPlugins().hasPlugin("java")) {
            TaskCollection<JavaCompile> javaCompilationTasks = project.getTasks().withType(JavaCompile.class);
            CompileJavaccTask compileJavaccTask = (CompileJavaccTask) project.getTasks().findByName(CompileJavaccTask.TASK_NAME_VALUE);
            if (compileJavaccTask != null) {
                addJavaccDependencyToJavaCompileTask(javaCompilationTasks, compileJavaccTask);
            }
        }
    }

    private void addJavaccDependencyToJavaCompileTask(TaskCollection<JavaCompile> javaCompilationTasks, CompileJavaccTask compileJavaccTask) {
        for (JavaCompile task : javaCompilationTasks) {
            task.dependsOn(compileJavaccTask);
            task.source(compileJavaccTask.getOutputDirectory());
        }
    }
}
