package org.javacc.plugin.gradle.javacc.compiler;

import java.io.File;

import org.gradle.api.file.FileTree;

/**
 * Represents the input and output configuration of a compiler.
 */
public interface CompilerInputOutputConfiguration {

    /**
     * @return the directory where the compiler should output its final output
     */
    File getOutputDirectory();

    /**
     * @return the directory where the compiler should read the source files
     */
    File getInputDirectory();

    /**
     * @return the temporary directory where the compiler should output intermediate work
     */
    File getTempOutputDirectory();

    /**
     * @return a {@link FileTree} representation of all the source files specifically for the compiler
     */
    FileTree getSource();

    /**
     * @return a {@link FileTree} representation of the Java source files that may be used by the compiler
     */
    FileTree getJavaSourceTree();

}
