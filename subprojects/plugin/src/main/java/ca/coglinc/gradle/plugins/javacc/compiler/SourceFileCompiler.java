package ca.coglinc.gradle.plugins.javacc.compiler;

import java.io.File;

import org.gradle.api.file.RelativePath;
import org.gradle.api.logging.Logger;

/**
 * Implementations invoke a program to compile source files.
 *
 * @see {@link ca.coglinc.gradle.plugins.javacc.programexecution.ProgramInvoker}
 */
public interface SourceFileCompiler {

    void compileSourceFilesToTempOutputDirectory();

    void copyCompiledFilesFromTempOutputDirectoryToOutputDirectory();

    void copyNonJavaccFilesToOutputDirectory();

    void compile(File inputDirectory, RelativePath inputRelativePath);

    String supportedSuffix();

    String getProgramName();

    File getOutputDirectory();

    File getInputDirectory();

    Logger getLogger();

    void createTempOutputDirectory();

    void cleanTempOutputDirectory();
}
