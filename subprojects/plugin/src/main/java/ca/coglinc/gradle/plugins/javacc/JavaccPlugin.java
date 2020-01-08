package ca.coglinc.gradle.plugins.javacc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;

public class JavaccPlugin implements Plugin<Project> {
    public static final String GROUP = "JavaCC";

    @Override
    public void apply(Project project) {
        Configuration configuration = createJavaccConfiguration(project);
        configureDefaultJavaccDependency(project, configuration);

        addCompileJavaccTaskToProject(project, configuration);
        addCompileJJTreeTaskToProject(project, configuration);
        addCompileJjdocTaskToProject(project, configuration);

        configureTaskDependencies(project);
    }

    private Configuration createJavaccConfiguration(Project project) {
        Configuration configuration = project.getConfigurations().create("javacc");
        configuration.setVisible(false);
        configuration.setTransitive(true);
        configuration.setDescription("The javacc dependencies to be used.");
        return configuration;
    }

    private void configureDefaultJavaccDependency(final Project project, Configuration configuration) {
        configuration.defaultDependencies(new Action<DependencySet>() {
            @Override
            public void execute(DependencySet dependencies) {
                dependencies.add(project.getDependencies().create("net.java.dev.javacc:javacc:6.1.2"));
            }
        });
    }

    private void addCompileJavaccTaskToProject(Project project, Configuration configuration) {
        addTaskToProject(project, CompileJavaccTask.class, CompileJavaccTask.TASK_NAME_VALUE, CompileJavaccTask.TASK_DESCRIPTION_VALUE,
            JavaccPlugin.GROUP, configuration);
    }

    private void addCompileJJTreeTaskToProject(Project project, Configuration configuration) {
        addTaskToProject(project, CompileJjtreeTask.class, CompileJjtreeTask.TASK_NAME_VALUE, CompileJjtreeTask.TASK_DESCRIPTION_VALUE,
            JavaccPlugin.GROUP, configuration);
    }

    private void addCompileJjdocTaskToProject(Project project, Configuration configuration) {
        addTaskToProject(project, CompileJjdocTask.class, CompileJjdocTask.TASK_NAME_VALUE, CompileJjdocTask.TASK_DESCRIPTION_VALUE,
            JavaccPlugin.GROUP, configuration);
    }

    private void addTaskToProject(Project project, Class<? extends AbstractJavaccTask> type, String name, String description, String group, final Configuration configuration) {
        Map<String, Object> options = new HashMap<String, Object>(3);

        options.put(Task.TASK_TYPE, type);
        options.put(Task.TASK_DESCRIPTION, description);
        options.put(Task.TASK_GROUP, group);

        AbstractJavaccTask task = (AbstractJavaccTask) project.task(options, name);
        task.getConventionMapping().map("classpath", returning(configuration));
    }

    private void configureTaskDependencies(Project project) {
        JavaToJavaccDependencyAction compileJavaDependsOnCompileJavacc = new JavaToJavaccDependencyAction();
        project.afterEvaluate(compileJavaDependsOnCompileJavacc);
    }

    private static <T> Callable<T> returning(final T value) {
        return new Callable<T>() {
            @Override
            public T call() {
                return value;
            }
        };
    }

}
