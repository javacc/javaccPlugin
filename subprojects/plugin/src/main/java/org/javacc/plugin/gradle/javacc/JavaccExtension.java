package org.javacc.plugin.gradle.javacc;

import java.util.Collection;

import org.gradle.api.tasks.SourceSet;

public class JavaccExtension {
    public static final String JAVACC_EXTENSION_NAME = "javacc";
    public Collection<SourceSet> dependentSourceSets;
}
