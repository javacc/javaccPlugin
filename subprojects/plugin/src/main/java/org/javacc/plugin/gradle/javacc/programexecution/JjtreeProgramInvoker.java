package org.javacc.plugin.gradle.javacc.programexecution;

import java.io.File;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RelativePath;
import org.gradle.process.ExecOperations;

/**
 * This {@link ProgramInvoker} implementation invokes the JJTree program to generate JavaCC grammar files from JJTree files.
 */
public class JjtreeProgramInvoker extends AbstractProgramInvoker {
    public static final String SUPPORTED_FILE_SUFFIX = ".jjt";
    private final String outputDirectoryArgName;

    public JjtreeProgramInvoker(FileCollection classpath, String javaccVersion, File tempOutputDirectory,
           ExecOperations execOperations) {
        super(classpath, javaccVersion, tempOutputDirectory, JjtreeExecutorAction.class, execOperations);
        outputDirectoryArgName = getOutputDirectoryArgName();
    }

    private String getOutputDirectoryArgName() {
        Integer[] version = getJavaccVersion();
        if (version[0] == VERSION_4 && version[1] <= 0) {
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
