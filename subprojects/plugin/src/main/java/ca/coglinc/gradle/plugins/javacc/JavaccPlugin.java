package ca.coglinc.gradle.plugins.javacc;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class JavaccPlugin implements Plugin<Project> {

    public static final String GROUP = "JavaCC";

    @Override
    public void apply(Project project) {
        addCompileJavaccTaskToProject(project);
        addCompileJJTreeTaskToProject(project);

        JavaToJavaccDependencyAction compileJavaDependsOnCompileJavacc = new JavaToJavaccDependencyAction();
        project.afterEvaluate(compileJavaDependsOnCompileJavacc);
    }

    private void addCompileJavaccTaskToProject(Project project) {
        addTaskToProject(project, CompileJavaccTask.class, CompileJavaccTask.TASK_NAME_VALUE, CompileJavaccTask.TASK_DESCRIPTION_VALUE, GROUP);
    }

    private void addCompileJJTreeTaskToProject(Project project) {
        addTaskToProject(project, CompileJJTreeTask.class, CompileJJTreeTask.TASK_NAME_VALUE, CompileJJTreeTask.TASK_DESCRIPTION_VALUE, GROUP);
    }

    private void addTaskToProject(Project project, Class<?> type, String name, String description, String group) {
        Map<String, Object> options = new HashMap<String, Object>(2);

        options.put(Task.TASK_TYPE, type);
        options.put(Task.TASK_DESCRIPTION, description);
        options.put(Task.TASK_GROUP, group);

        project.task(options, name);
    }
}
