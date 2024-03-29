package org.javacc.plugin.gradle.javacc.compilationresults;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.gradle.api.logging.Logger;

public class CompiledJavaccFilesDirectory {
    private File outputDirectory;
    private File targetDirectory;
    private Logger logger;

    CompiledJavaccFilesDirectory(File outputDirectory, File targetDirectory, Logger logger) {
        this.outputDirectory = outputDirectory;
        this.targetDirectory = targetDirectory;
        this.logger = logger;
    }

    public Collection<CompiledJavaccFile> listFiles() {
        Collection<File> files = FileUtils.listFiles(outputDirectory, TrueFileFilter.TRUE, TrueFileFilter.TRUE);
        Collection<CompiledJavaccFile> compiledJavaccFiles = new ArrayList<>();

        for (File file : files) {
            CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, targetDirectory, logger);
            compiledJavaccFiles.add(compiledJavaccFile);
        }

        return compiledJavaccFiles;
    }

    @Override
    public String toString() {
        return outputDirectory.getAbsolutePath();
    }
}
