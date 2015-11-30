package ca.coglinc.gradle.plugins.javacc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;

public class JavaToJavaccDependencyAction implements Action<Project> {

	private DependencyConfigurationExtension extension;
	private Collection<JavaCompile> _javaDependentTasks = new ArrayList<JavaCompile>();
	
    @Override
    public void execute(Project project) {
        if (!project.getPlugins().hasPlugin("java")) {
            return;
        }

        extension = (DependencyConfigurationExtension) project.getExtensions().findByName(DependencyConfigurationExtension.dependencyConfigurationExtensionName);
        
        configureDependencyConfigurationExtension(project);
        
        configureJavaDependentTasks(project);
        
        configureCompileJJTreeTask(project);
        configureCompileJavaccTask(project);
    }

    private void configureDependencyConfigurationExtension(Project project) {
		if(extension != null && extension.sourceSets == null) {
			//initialize sourcesets
			JavaPluginConvention javaPluginConvention = (JavaPluginConvention)project.getConvention().findPlugin(JavaPluginConvention.class);
			if(javaPluginConvention != null)
				extension.sourceSets = javaPluginConvention.getSourceSets();
			else
				extension.sourceSets = Collections.emptyList();
		}
	}
    
    private void configureJavaDependentTasks(Project project) {
    	if(extension != null) {
			//use only java compile tasks from configured sourcesets. 
			for(SourceSet sourceSet : extension.sourceSets) {
				_javaDependentTasks.add( (JavaCompile) project.getTasks().findByName(sourceSet.getCompileJavaTaskName()) );
			}
    	}
	}

	private void configureCompileJJTreeTask(Project project) {
        CompileJjTreeTask compileJJTreeTask = (CompileJjTreeTask) project.getTasks().findByName(CompileJjTreeTask.TASK_NAME_VALUE);
        if (compileJJTreeTask == null) {
            return;
        }

        if (!compileJJTreeTask.getSource().isEmpty()) {
            addJJTreeDependencyToJavaccCompileTask(project.getTasks().withType(CompileJavaccTask.class), compileJJTreeTask);
        }
    }

    private void configureCompileJavaccTask(Project project) {
        CompileJavaccTask compileJavaccTask = (CompileJavaccTask) project.getTasks().findByName(CompileJavaccTask.TASK_NAME_VALUE);
        if (compileJavaccTask != null) {
            addJavaccDependencyToJavaCompileTask(compileJavaccTask);
        }
    }

    private void addJavaccDependencyToJavaCompileTask(CompileJavaccTask compileJavaccTask) {
        for (JavaCompile task : _javaDependentTasks) {
            task.dependsOn(compileJavaccTask);
            task.source(compileJavaccTask.getOutputDirectory());
        }
    }

    private void addJJTreeDependencyToJavaccCompileTask(TaskCollection<CompileJavaccTask> javaccCompilationTasks, CompileJjTreeTask compileJJTreeTask) {
 	 
        for (JavaCompile task : _javaDependentTasks) {
            task.dependsOn(compileJJTreeTask);
            task.source(compileJJTreeTask.getOutputDirectory());
        }

        for (CompileJavaccTask task : javaccCompilationTasks) {
            task.dependsOn(compileJJTreeTask);
            task.source(compileJJTreeTask.getOutputDirectory());
        }
    }
}
