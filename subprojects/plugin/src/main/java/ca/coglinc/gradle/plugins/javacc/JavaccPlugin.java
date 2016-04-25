package ca.coglinc.gradle.plugins.javacc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class JavaccPlugin implements Plugin<Project> {

    public static final String GROUP = "JavaCC";

    @Override
    public void apply(Project project) {
        addCompileJavaccTaskToProject(project);
        addCompileJJTreeTaskToProject(project);
        addCompileJjdocTaskToProject(project);

        JavaToJavaccDependencyAction compileJavaDependsOnCompileJavacc = new JavaToJavaccDependencyAction();
        project.afterEvaluate(compileJavaDependsOnCompileJavacc);
    }

    private void addCompileJavaccTaskToProject(Project project) {
        addTaskToProject(project, CompileJavaccTask.class, CompileJavaccTask.TASK_NAME_VALUE, CompileJavaccTask.TASK_DESCRIPTION_VALUE,
            JavaccPlugin.GROUP);
    }

    private void addCompileJJTreeTaskToProject(Project project) {
        addTaskToProject(project, CompileJjTreeTask.class, CompileJjTreeTask.TASK_NAME_VALUE, CompileJjTreeTask.TASK_DESCRIPTION_VALUE,
            JavaccPlugin.GROUP);
    }
    
    private void addCompileJjdocTaskToProject(Project project) {
        addTaskToProject(project, CompileJjdocTask.class, CompileJjdocTask.TASK_NAME_VALUE, CompileJjdocTask.TASK_DESCRIPTION_VALUE,
            JavaccPlugin.GROUP);
    }

    private void addTaskToProject(Project project, Class<?> type, String name, String description, String group) {
        Map<String, Object> options = new HashMap<String, Object>(2);

        options.put(Task.TASK_TYPE, type);
        options.put(Task.TASK_DESCRIPTION, description);
        options.put(Task.TASK_GROUP, group);

        project.task(options, name);

        Task cleanTask = project.getTasks().findByName("clean");
        if (cleanTask != null) {
            cleanTask.dependsOn("clean" + StringUtils.capitalize(CompileJavaccTask.TASK_NAME_VALUE));
        }
    }
}
