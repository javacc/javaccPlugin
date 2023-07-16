package org.javacc.plugin.gradle.javacc.programexecution;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.file.RelativePath;

/**
 * This {@link ProgramInvoker} implementation invokes the JJTree program to generate JavaCC grammar files from JJTree files.
 */
public class JjtreeProgramInvoker extends AbstractProgramInvoker {
    public static final String SUPPORTED_FILE_SUFFIX = ".jjt";
    private final String outputDirectoryArgName;

    public JjtreeProgramInvoker(Project project, Configuration classpath, File tempOutputDirectory) {
        super(project, classpath, tempOutputDirectory, JjtreeExecutorAction.class);
        outputDirectoryArgName = getOutputDirectoryArgName();
    }

    private String getOutputDirectoryArgName() {
        Integer[] version = getJavaccVersion();
        if (version[0] == 4 && version[1] <=0) {
           return "OUTPUT_DIRECTORY";
        }
        return "JJTREE_OUTPUT_DIRECTORY";
    }

    @Override
    public ProgramArguments augmentArguments(File inputDirectory, RelativePath inputRelativePath, ProgramArguments arguments) {
        ProgramArguments augmentedArguments = new ProgramArguments(arguments);
        augmentedArguments.add(outputDirectoryArgName, inputRelativePath.getFile(tempOutputDirectory).getParentFile().getAbsolutePath());
        addCodeGenerator(augmentedArguments);
        return augmentedArguments;
    }

    @Override
    public String getProgramName() {
        return "JJTree";
    }

    @Override
    public String supportedSuffix() {
        return SUPPORTED_FILE_SUFFIX;
    }
}
