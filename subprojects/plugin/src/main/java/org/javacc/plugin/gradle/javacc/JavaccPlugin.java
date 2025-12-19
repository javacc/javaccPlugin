package org.javacc.plugin.gradle.javacc;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;

public class JavaccPlugin implements Plugin<Project> {
    public static final String GROUP = "JavaCC";

    @Override
    public void apply(Project project) {
        Configuration configuration = createJavaccConfiguration(project);
        configureDefaultJavaccDependency(project, configuration);

        addCompileJavaccTaskToProject(project, configuration);
        addCompileJJTreeTaskToProject(project, configuration);
        addCompileJjdocTaskToProject(project, configuration);
        project.getExtensions().create(JavaccExtension.JAVACC_EXTENSION_NAME, JavaccExtension.class);
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
        configuration.defaultDependencies(dependencies ->
            dependencies.add(project.getDependencies().create("net.java.dev.javacc:javacc:7.0.13")));
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

    private void addTaskToProject(Project project, Class<? extends AbstractJavaccTask> type, String name, String description, String group, Configuration configuration) {
        project.getTasks().register(name, type, t -> {
            t.setDescription(description);
            t.setGroup(group);

            t.getClasspath().from(configuration);
            t.getJavaccVersion().set(project.provider(() -> {
                for (Dependency dependency : configuration.getAllDependencies()) {
                    String id = dependency.getGroup() + ":" + dependency.getName();
                    if (dependency.getVersion() != null
                        && ("net.java.dev.javacc:javacc".equals(id)
                        || "org.javacc.generator:java".equals(id))) {
                        return dependency.getVersion();
                    }
                }
                return "";
            }));
        });
    }

    private void configureTaskDependencies(Project project) {
        JavaToJavaccDependencyAction compileJavaDependsOnCompileJavacc = new JavaToJavaccDependencyAction();
        project.afterEvaluate(compileJavaDependsOnCompileJavacc);
    }
}
