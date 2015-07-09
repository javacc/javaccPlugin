package ca.coglinc.gradle.plugins.javacc.compilationresults;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.gradle.api.file.FileTree;
import org.gradle.api.logging.Logger;

public class CompiledJavaccFile {
    private static final Pattern PACKAGE_DECLARATION_PATTERN = Pattern.compile("package\\s+([^\\s.;]+(\\.[^\\s.;]+)*)\\s*;");
    
    private File compiledJavaccFile;
    private File outputDirectory;
    private FileTree customAstClassesDirectory;
    private File targetDirectory;
    private Logger logger;

    CompiledJavaccFile(File file, File outputDirectory, FileTree customAstClassesDirectory, File targetDirectory, Logger logger) {
        this.compiledJavaccFile = file;
        this.outputDirectory = outputDirectory;
        this.customAstClassesDirectory = customAstClassesDirectory;
        this.targetDirectory = targetDirectory;
        this.logger = logger;
    }

    public boolean customAstClassExists() {
        File customAstClassInputFile = getCustomAstClassInputFile();

        return (customAstClassInputFile != null) && customAstClassInputFile.exists();
    }

    private File getCustomAstClassInputFile() {
        String compiledJavaccFilePackage = getPackageName(compiledJavaccFile);
        
        Collection<File> sourceFiles = customAstClassesDirectory.getFiles();
        return scanSourceFiles(compiledJavaccFilePackage, sourceFiles);
    }

    private File scanSourceFiles(String compiledJavaccFilePackage, Collection<File> sourceFiles) {
        for (File sourceFile : sourceFiles) {
            logger.debug("Scanning source file [{}] looking for a file named [{}] in package [{}]", sourceFile, compiledJavaccFile.getName(), compiledJavaccFilePackage);
            if (sourceFile.isDirectory()) {
                Collection<File> childFiles = FileUtils.listFiles(sourceFile, FileFilterUtils.suffixFileFilter(".java"), TrueFileFilter.TRUE);
                File matchingChildFile = scanSourceFiles(compiledJavaccFilePackage, childFiles);
                if (matchingChildFile != null) {
                    return matchingChildFile;
                }
            } else {
                if (FilenameUtils.isExtension(sourceFile.getName(), "java") && compiledJavaccFile.getName().equals(sourceFile.getName())) {
                    String packageName = getPackageName(sourceFile);
                    
                    if (compiledJavaccFilePackage.equals(packageName)) {
                        return sourceFile;
                    }
                }
            }
        }
        
        return null;
    }
    
    private String getPackageName(File file) {
        String fileContents = "";
        try {
            fileContents = FileUtils.readFileToString(file, Charsets.UTF_8.name());
        } catch (IOException e) {
            logger.warn("Could not read file contents for file [{}]", file);
        }
        
        Matcher matcher = PACKAGE_DECLARATION_PATTERN.matcher(fileContents);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return "";
    }

    public void copyCompiledFileToTargetDirectory() {
        logger.debug("Custom AST class {} not found", getCustomAstClassInputFile());
        
        File destination = new File(compiledJavaccFile.getAbsolutePath().replace(outputDirectory.getAbsolutePath(), targetDirectory.getAbsolutePath()));
        logger.debug("Moving compiled file {} to {}", compiledJavaccFile, destination);
        
        try {
            FileUtils.moveFile(compiledJavaccFile, destination);
        } catch (IOException e) {
            String errorMessage = String.format("Could not copy %s from %s to %s", compiledJavaccFile, outputDirectory, targetDirectory);
            throw new CompiledJavaccFileOperationException(errorMessage, e);
        }
    }

    public void copyCustomAstClassToTargetDirectory() {
        logger.debug("Not copying compiled file {} from {} to {} because it is overridden by the custom AST class {}", compiledJavaccFile, outputDirectory, targetDirectory,
            getCustomAstClassInputFile());
        
        try {
            String packagePath = getPackageName(compiledJavaccFile).replace("\\.", File.separator);
            File destination = new File(targetDirectory.getAbsolutePath() + File.separator + packagePath, compiledJavaccFile.getName());
            FileUtils.copyFile(getCustomAstClassInputFile(), destination);
        } catch (IOException e) {
            String errorMessage = String.format("Could not copy %s to %s", getCustomAstClassInputFile(), targetDirectory);
            throw new CompiledJavaccFileOperationException(errorMessage, e);
        }
    }
}
