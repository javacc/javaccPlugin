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
        project.task(compileJavaccTaskOptions(), CompileJavaccTask.TASK_NAME_VALUE);
        project.task(compileJJTreeTaskOptions(), CompileJJTreeTask.TASK_NAME_VALUE);

        JavaToJavaccDependencyAction compileJavaDependsOnCompileJavacc = new JavaToJavaccDependencyAction();
        project.afterEvaluate(compileJavaDependsOnCompileJavacc);
    }

    private Map<String, ?> compileJavaccTaskOptions() {
        Map<String, Object> options = new HashMap<String, Object>(2);

        options.put(Task.TASK_TYPE, CompileJavaccTask.class);
        options.put(Task.TASK_DESCRIPTION, CompileJavaccTask.TASK_DESCRIPTION_VALUE);
        options.put(Task.TASK_GROUP, GROUP);

        return options;
    }

    private Map<String, ?> compileJJTreeTaskOptions() {
        Map<String, Object> options = new HashMap<String, Object>(2);

        options.put(Task.TASK_TYPE, CompileJJTreeTask.class);
        options.put(Task.TASK_DESCRIPTION, CompileJJTreeTask.TASK_DESCRIPTION_VALUE);
        options.put(Task.TASK_GROUP, GROUP);

        return options;
    }
}
