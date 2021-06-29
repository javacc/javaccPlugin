package org.javacc.plugin.gradle.javacc.programexecution;

import java.io.File;

import org.gradle.api.file.RelativePath;

/**
 * Implementations invoke a specific program runtime to compile source files.
 */
public interface ProgramInvoker {
    /**
     * Invokes the program's runtime with the provided arguments.
     *
     * @param arguments
     *              The arguments to provide the program with
     * @throws Exception
     *              If the program fails with a non-zero exit value
     */
    void invokeCompiler(ProgramArguments arguments) throws Exception;

    /**
     * Gives a chance to implementations to add some required arguments for example, the output directory.
     *
     * @param inputDirectory
     *            The input directory from which input relative path is derived.
     * @param inputRelativePath
     *            The input path relative to the input directory. This is the file that will be "compiled".
     * @param arguments
     *            The ProgramArguments to add new arguments to.
     *
     * @return the augmented ProgramArguments
     */
    ProgramArguments augmentArguments(File inputDirectory, RelativePath inputRelativePath, ProgramArguments arguments);

    /**
     * @return the name of the actual program used to compile source files
     */
    String getProgramName();

    /**
     * @return the file extension, including the "dot" (.), of source files compiled by this program
     */
    String supportedSuffix();
}
