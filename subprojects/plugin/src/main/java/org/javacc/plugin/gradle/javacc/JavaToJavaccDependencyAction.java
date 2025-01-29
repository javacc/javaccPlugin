package org.javacc.plugin.gradle.javacc;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        Collection<JavaCompile> javaTasks = project.getTasks().withType(JavaCompile.class)
            .stream().filter(getJavaTaskFilter(project)).collect(Collectors.toList());
        configureCompileJJTreeTask(project, javaTasks);
        configureCompileJavaccTask(project, javaTasks);
    }

    private Predicate<? super JavaCompile> getJavaTaskFilter(Project project) {
        JavaccExtension extension = (JavaccExtension) project.getExtensions()
            .findByName(JavaccExtension.JAVACC_EXTENSION_NAME);
        if (extension != null && extension.dependentSourceSets != null) {
            return task -> extension.dependentSourceSets.stream()
                .anyMatch(set -> set.getCompileJavaTaskName().equals(task.getName()));
        }
        return any -> true;
    }

    private void configureCompileJJTreeTask(Project project, Collection<JavaCompile> javaTasks) {
        CompileJjtreeTask compileJjtreeTask = (CompileJjtreeTask) project.getTasks().findByName(CompileJjtreeTask.TASK_NAME_VALUE);
        if (compileJjtreeTask == null) {
            return;
        }

        if (!compileJjtreeTask.getSource().isEmpty()) {
            addJJTreeDependencyToJavaccCompileTask(javaTasks,
                project.getTasks().withType(CompileJavaccTask.class), compileJjtreeTask);
        }
    }

    private void configureCompileJavaccTask(Project project, Collection<JavaCompile> javaTasks) {
        CompileJavaccTask compileJavaccTask = (CompileJavaccTask) project.getTasks().findByName(CompileJavaccTask.TASK_NAME_VALUE);
        if (compileJavaccTask != null) {
            addJavaccDependencyToJavaCompileTask(javaTasks, compileJavaccTask);
        }
    }

    private void addJavaccDependencyToJavaCompileTask(Collection<JavaCompile> javaCompilationTasks, CompileJavaccTask compileJavaccTask) {
        for (JavaCompile task : javaCompilationTasks) {
            task.dependsOn(compileJavaccTask);
            task.source(compileJavaccTask.getOutputDirectory());
            compileJavaccTask.addJavaSources(task.getSource());
        }
    }

    private void addJJTreeDependencyToJavaccCompileTask(Collection<JavaCompile> javaCompilationTasks,
                                                        TaskCollection<CompileJavaccTask> javaccCompilationTasks, CompileJjtreeTask compileJjtreeTask) {
        for (JavaCompile task : javaCompilationTasks) {
            task.dependsOn(compileJjtreeTask);
            task.source(compileJjtreeTask.getOutputDirectory());
            compileJjtreeTask.addJavaSources(task.getSource());
        }

        for (CompileJavaccTask task : javaccCompilationTasks) {
            task.dependsOn(compileJjtreeTask);
            task.source(compileJjtreeTask.getOutputDirectory());
        }
    }
}
