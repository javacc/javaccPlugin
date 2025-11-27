package org.javacc.plugin.gradle.javacc.programexecution;

import java.io.File;
import java.util.Arrays;
import java.util.function.Predicate;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RelativePath;
import org.gradle.process.ExecOperations;

/**
 * This {@link ProgramInvoker} implementation invokes the JJDoc program to generate grammar representations from JavaCC files.
 */
public class JjdocProgramInvoker extends AbstractProgramInvoker {
    public static final String SUPPORTED_FILE_SUFFIX = ".jj";

    public JjdocProgramInvoker(FileCollection classpath, String javaccVersion, File tempOutputDirectory, ExecOperations execOperations) {
        super(classpath, javaccVersion, tempOutputDirectory, JjdocExecutorAction.class, execOperations);
    }

    @Override
    public ProgramArguments augmentArguments(File inputDirectory, RelativePath inputRelativePath, ProgramArguments arguments) {
        File tempOutputFile = inputRelativePath.getFile(tempOutputDirectory);
        tempOutputFile.mkdirs();

        String outputFileExtension = getJjdocOutputFileExtension(arguments);

        ProgramArguments augmentedArguments = new ProgramArguments(arguments);
        augmentedArguments.add("OUTPUT_FILE", tempOutputFile.getAbsolutePath().replace(SUPPORTED_FILE_SUFFIX, outputFileExtension));

        return augmentedArguments;
    }

    private String getJjdocOutputFileExtension(ProgramArguments arguments) {
        String outputFileExtension = ".html";
        if (Arrays.stream(arguments.toArray()).anyMatch(outputTextPredicate())) {
            outputFileExtension = ".txt";
        }
        return outputFileExtension;
    }

    private Predicate<String> outputTextPredicate() {
        return "-text=true"::equalsIgnoreCase;
    }

    @Override
    public String getProgramName() {
        return "JJDoc";
    }

    @Override
    public String supportedSuffix() {
        return SUPPORTED_FILE_SUFFIX;
    }
}
