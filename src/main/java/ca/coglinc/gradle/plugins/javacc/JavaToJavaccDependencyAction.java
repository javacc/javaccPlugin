package ca.coglinc.gradle.plugins.javacc;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;

public class JavaToJavaccDependencyAction implements Action<Project> {

    public void execute(Project project) {
        if (project.getPlugins().hasPlugin(JavaccPlugin.class)) {
            TaskCollection<JavaCompile> javaCompilationTasks = project.getTasks().withType(JavaCompile.class);
            CompileJavaccTask compileJavaccTask = (CompileJavaccTask) project.getTasks().findByName(CompileJavaccTask.TASK_NAME_VALUE);
            for (JavaCompile task : javaCompilationTasks) {
                task.dependsOn(compileJavaccTask);
                task.source(compileJavaccTask.getOutputDirectory());
            }
        }
    }
}
