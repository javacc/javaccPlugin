package ca.coglinc.gradle.plugins.javacc;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;

public class JavaToJavaccDependencyAction implements Action<Project> {

	private DependencyConfigurationExtension extension;
	
    @Override
    public void execute(Project project) {
        if (!project.getPlugins().hasPlugin("java")) {
            return;
        }

        extension = (DependencyConfigurationExtension) project.getExtensions().getByName(DependencyConfigurationExtension.DEPENDENCYCONFIGURATIONEXTENSION_NAME);
        
        configureCompileJJTreeTask(project);
        configureCompileJavaccTask(project);
    }

    private void configureCompileJJTreeTask(Project project) {
        CompileJjTreeTask compileJJTreeTask = (CompileJjTreeTask) project.getTasks().findByName(CompileJjTreeTask.TASK_NAME_VALUE);
        if (compileJJTreeTask == null) {
            return;
        }

        if (!compileJJTreeTask.getSource().isEmpty()) {
            addJJTreeDependencyToJavaccCompileTask(project.getTasks().withType(JavaCompile.class),
                project.getTasks().withType(CompileJavaccTask.class), compileJJTreeTask);
        }
    }

    private void configureCompileJavaccTask(Project project) {
        CompileJavaccTask compileJavaccTask = (CompileJavaccTask) project.getTasks().findByName(CompileJavaccTask.TASK_NAME_VALUE);
        if (compileJavaccTask != null) {
            addJavaccDependencyToJavaCompileTask(project.getTasks().withType(JavaCompile.class), compileJavaccTask);
        }
    }

    private void addJavaccDependencyToJavaCompileTask(TaskCollection<JavaCompile> javaCompilationTasks, CompileJavaccTask compileJavaccTask) {
    	
    	TaskCollection<JavaCompile> computedJavaCompilationTasks = null;
    	if(extension.compileTasksDependenciesForJavacc != null) {
    		computedJavaCompilationTasks = extension.compileTasksDependenciesForJavacc.call(javaCompilationTasks, compileJavaccTask);
    	}    	 
        for (JavaCompile task : computedJavaCompilationTasks == null ? javaCompilationTasks : computedJavaCompilationTasks) {
            task.dependsOn(compileJavaccTask);
            task.source(compileJavaccTask.getOutputDirectory());
        }
    }

    private void addJJTreeDependencyToJavaccCompileTask(TaskCollection<JavaCompile> javaCompilationTasks,
        TaskCollection<CompileJavaccTask> javaccCompilationTasks, CompileJjTreeTask compileJJTreeTask) {
    	TaskCollection<JavaCompile> computedJavaCompilationTasks = null;
    	if(extension.compileTasksDependenciesForJjTree != null) { 
    		computedJavaCompilationTasks = extension.compileTasksDependenciesForJjTree.call(javaCompilationTasks, compileJJTreeTask);
    	}    	 
        for (JavaCompile task : computedJavaCompilationTasks == null ? javaCompilationTasks : computedJavaCompilationTasks) {
            task.dependsOn(compileJJTreeTask);
            task.source(compileJJTreeTask.getOutputDirectory());
        }

        for (CompileJavaccTask task : javaccCompilationTasks) {
            task.dependsOn(compileJJTreeTask);
            task.source(compileJJTreeTask.getOutputDirectory());
        }
    }
}
