package ca.coglinc.gradle.plugins.javacc;

import java.util.Collection;

import org.gradle.api.tasks.SourceSet;

public class DependencyConfigurationExtension {
	public static final String dependencyConfigurationExtensionName = "javaccDependencyConfiguration";
	
	public Collection<SourceSet> sourceSets;
}
