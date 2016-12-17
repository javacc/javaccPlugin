package ca.coglinc.gradle.plugins.javacc.compilationresults;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.gradle.api.file.FileTree;
import org.gradle.api.logging.Logger;

import ca.coglinc.gradle.plugins.javacc.Language;

public class CompiledJavaccFilesDirectory {
    private final File outputDirectory;
    private final FileTree customAstClassesDirectory;
    private final File targetDirectory;
    private final Logger logger;
    private final Language language;
    
    CompiledJavaccFilesDirectory(Language language, File outputDirectory, FileTree customAstClassesDirectory, File targetDirectory, Logger logger) {
        this.language = language;
        this.outputDirectory = outputDirectory;
        this.customAstClassesDirectory = customAstClassesDirectory;
        this.targetDirectory = targetDirectory;
        this.logger = logger;
    }

    public Collection<CompiledJavaccFile> listFiles() {
        Collection<File> files = FileUtils.listFiles(outputDirectory, TrueFileFilter.TRUE, TrueFileFilter.TRUE);
        Collection<CompiledJavaccFile> compiledJavaccFiles = new ArrayList<CompiledJavaccFile>();
        
        for (File file : files) {
            CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(language, file, outputDirectory, customAstClassesDirectory, targetDirectory, logger);
            compiledJavaccFiles.add(compiledJavaccFile);
        }
        
        return compiledJavaccFiles;
    }
    
    @Override
    public String toString() {
        return outputDirectory.getAbsolutePath();
    }
}
