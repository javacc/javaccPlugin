package ca.coglinc.gradle.plugins.javacc;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.language.cpp.tasks.CppCompile;

public class JavaToJavaccDependencyAction implements Action<Project> {

    @Override
    public void execute(Project project) {
        if (project.getPlugins().hasPlugin("java") || project.getPlugins().hasPlugin("cpp")) {
            configureCompileJJTreeTask(project);
            configureCompileJavaccTask(project);
        }
    }

    private void configureCompileJJTreeTask(Project project) {
        CompileJjTreeTask compileJJTreeTask = (CompileJjTreeTask) project.getTasks().findByName(CompileJjTreeTask.TASK_NAME_VALUE);
        if (compileJJTreeTask == null) {
            return;
        }

        if (!compileJJTreeTask.getSource().isEmpty()) {
            if (project.getPlugins().hasPlugin("java"))
               addJJTreeDependencyToJavaCompileTask(project.getTasks().withType(JavaCompile.class),
                   project.getTasks().withType(CompileJavaccTask.class), compileJJTreeTask);
            

            if (project.getPlugins().hasPlugin("cpp"))
                addJJTreeDependencyToCppCompileTask(project.getTasks().withType(CppCompile.class),
                    project.getTasks().withType(CompileJavaccTask.class), compileJJTreeTask);
            
        }
    }

    private void configureCompileJavaccTask(Project project) {
        CompileJavaccTask compileJavaccTask = (CompileJavaccTask) project.getTasks().findByName(CompileJavaccTask.TASK_NAME_VALUE);
        
        if (compileJavaccTask != null) {
            if (project.getPlugins().hasPlugin("java"))
                addJavaccDependencyToJavaCompileTask(project.getTasks().withType(JavaCompile.class), compileJavaccTask);

            if (project.getPlugins().hasPlugin("cpp"))
                addJavaccDependencyToCppCompileTask(project.getTasks().withType(CppCompile.class), compileJavaccTask);
        }
    }

    private void addJavaccDependencyToJavaCompileTask(TaskCollection<JavaCompile> javaCompilationTasks, CompileJavaccTask compileJavaccTask) {
        for (JavaCompile task : javaCompilationTasks) {
            task.dependsOn(compileJavaccTask);
            task.source(compileJavaccTask.getOutputDirectory());
        }
    }

    private void addJavaccDependencyToCppCompileTask(TaskCollection<CppCompile> cppCompilationTasks, CompileJavaccTask compileJavaccTask) {
        for (CppCompile task : cppCompilationTasks) {
            task.dependsOn(compileJavaccTask);
            task.source(compileJavaccTask.getOutputDirectory());
        }
    }

    private void addJJTreeDependencyToJavaCompileTask(TaskCollection<JavaCompile> javaCompilationTasks,
        TaskCollection<CompileJavaccTask> javaccCompilationTasks, CompileJjTreeTask compileJJTreeTask) {
        for (JavaCompile task : javaCompilationTasks) {
            task.dependsOn(compileJJTreeTask);
            task.source(compileJJTreeTask.getOutputDirectory());
        }

        for (CompileJavaccTask task : javaccCompilationTasks) {
            task.dependsOn(compileJJTreeTask);
            task.source(compileJJTreeTask.getOutputDirectory());
        }
    }

    private void addJJTreeDependencyToCppCompileTask(TaskCollection<CppCompile> cppCompilationTasks,
        TaskCollection<CompileJavaccTask> javaccCompilationTasks, CompileJjTreeTask compileJJTreeTask) {

        for (CppCompile task : cppCompilationTasks) {
            task.dependsOn(compileJJTreeTask);
            task.source(compileJJTreeTask.getOutputDirectory());
        }

        for (CompileJavaccTask task : javaccCompilationTasks) {
            task.dependsOn(compileJJTreeTask);
            task.source(compileJJTreeTask.getOutputDirectory());
        }
    }
}
