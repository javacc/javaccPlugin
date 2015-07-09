package ca.coglinc.gradle.plugins.javacc.compilationresults;

import java.io.File;

import org.gradle.api.file.FileTree;
import org.gradle.api.logging.Logger;

public class CompiledJavaccFilesDirectoryFactory {

    public CompiledJavaccFilesDirectory getCompiledJavaccFilesDirectory(File outputDirectory, FileTree customAstClassesDirectory, File targetDirectory, Logger logger) {
        if ((outputDirectory == null) || !outputDirectory.exists() || !outputDirectory.isDirectory()) {
            throw new IllegalArgumentException("outputDirectory [" + outputDirectory + "] must be an existing directory");
        }
        
        if (customAstClassesDirectory == null) {
            throw new IllegalArgumentException("customAstClassesDirectory [" + outputDirectory + "] must not be null");
        }
        
        if ((targetDirectory == null) || !targetDirectory.exists() || !targetDirectory.isDirectory()) {
            throw new IllegalArgumentException("targetDirectory [" + targetDirectory + "] must be an existing directory");
        }
        
        return new CompiledJavaccFilesDirectory(outputDirectory, customAstClassesDirectory, targetDirectory, logger);
    }
}
