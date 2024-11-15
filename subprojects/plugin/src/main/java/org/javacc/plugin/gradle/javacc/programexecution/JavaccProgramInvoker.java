package org.javacc.plugin.gradle.javacc.programexecution;

import java.io.File;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.RelativePath;
import org.gradle.process.ExecOperations;

/**
 * This {@link ProgramInvoker} implementation invokes the JavaCC program to generate Java files from JavaCC grammar files.
 */
public class JavaccProgramInvoker extends AbstractProgramInvoker {
    public static final String SUPPORTED_FILE_SUFFIX = ".jj";

    public JavaccProgramInvoker(Configuration classpath, File tempOutputDirectory,
                                ExecOperations execOperations) {
        super(classpath, tempOutputDirectory, JavaccExecutorAction.class, execOperations);
    }

    @Override
    public ProgramArguments augmentArguments(File inputDirectory, RelativePath inputRelativePath, ProgramArguments arguments) {
        ProgramArguments augmentedArguments = new ProgramArguments(arguments);
        augmentedArguments.add("OUTPUT_DIRECTORY", inputRelativePath.getFile(tempOutputDirectory).getParentFile().getAbsolutePath());
        addCodeGenerator(augmentedArguments);
        return augmentedArguments;
    }

    @Override
    public String getProgramName() {
        return "JavaCC";
    }

    @Override
    public String supportedSuffix() {
        return SUPPORTED_FILE_SUFFIX;
    }
}
