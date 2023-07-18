package org.javacc.plugin.gradle.javacc.compilationresults;

import java.io.File;

import org.gradle.api.logging.Logger;

public class CompiledJavaccFilesDirectoryFactory {

    public CompiledJavaccFilesDirectory getCompiledJavaccFilesDirectory(File outputDirectory, File targetDirectory, Logger logger) {
        if ((outputDirectory == null) || !outputDirectory.exists() || !outputDirectory.isDirectory()) {
            throw new IllegalArgumentException("outputDirectory [" + outputDirectory + "] must be an existing directory");
        }

        if ((targetDirectory == null) || !targetDirectory.exists() || !targetDirectory.isDirectory()) {
            throw new IllegalArgumentException("targetDirectory [" + targetDirectory + "] must be an existing directory");
        }

        return new CompiledJavaccFilesDirectory(outputDirectory, targetDirectory, logger);
    }
}
