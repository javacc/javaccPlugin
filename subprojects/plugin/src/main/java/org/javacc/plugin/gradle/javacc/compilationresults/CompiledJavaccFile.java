package org.javacc.plugin.gradle.javacc.compilationresults;

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
import org.gradle.api.logging.Logger;

public class CompiledJavaccFile {
    private static final Pattern PACKAGE_DECLARATION_PATTERN = Pattern.compile("package\\s+([^\\s.;]+(\\.[^\\s.;]+)*)\\s*;");

    private File compiledJavaccFile;
    private File outputDirectory;
    private File targetDirectory;
    private Logger logger;

    public CompiledJavaccFile(File file, File outputDirectory, File targetDirectory, Logger logger) {
        this.compiledJavaccFile = file;
        this.outputDirectory = outputDirectory;
        this.targetDirectory = targetDirectory;
        this.logger = logger;
    }

    public File getCustomAstClassInputFile(Collection<File> sourceFiles) {
        File customAstClassInputFile;
        String compiledJavaccFilePackage = getPackageName(compiledJavaccFile);

        if (sourceFiles != null) {
            customAstClassInputFile = scanSourceFiles(compiledJavaccFilePackage, sourceFiles);
        } else {
            customAstClassInputFile = null;
        }

        return customAstClassInputFile != null && customAstClassInputFile.exists() ? customAstClassInputFile : null;
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
        logger.info("Custom AST class not found");

        File destination = new File(compiledJavaccFile.getAbsolutePath().replace(outputDirectory.getAbsolutePath(), targetDirectory.getAbsolutePath()));
        logger.info("Copying compiled file {} to {}", compiledJavaccFile, destination);

        try {
            FileUtils.copyFile(compiledJavaccFile, destination);
        } catch (IOException e) {
            String errorMessage = String.format("Could not copy %s from %s to %s", compiledJavaccFile, outputDirectory, targetDirectory);
            throw new CompiledJavaccFileOperationException(errorMessage, e);
        }
    }

    public void copyCustomAstClassToTargetDirectory(File customAstClassInputFile) {
        logger.info("Not copying compiled file {} from {} to {} because it is overridden by the custom AST class {}", compiledJavaccFile, outputDirectory, targetDirectory,
            customAstClassInputFile);

        String packagePath = getPackageName(compiledJavaccFile).replaceAll("\\.", Matcher.quoteReplacement(File.separator));
        File destination = new File(targetDirectory.getAbsolutePath() + File.separator + packagePath, compiledJavaccFile.getName());
        logger.info("Copying custom AST class [{}] to [{}]", customAstClassInputFile, destination);

        try {
            FileUtils.copyFile(customAstClassInputFile, destination);
        } catch (IOException e) {
            String errorMessage = String.format("Could not copy %s to %s", customAstClassInputFile, targetDirectory);
            throw new CompiledJavaccFileOperationException(errorMessage, e);
        }
    }

    public void ignoreCompiledFileAndUseCustomAstClassFromJavaSourceTree(File customAst) {
        logger.info("Ignoring compiled file {} because it is overridden by the custom AST class in Java source tree {}", compiledJavaccFile,
            customAst);
    }

    @Override
    public String toString() {
        return compiledJavaccFile.getAbsolutePath();
    }

    public boolean handleCustomAstInJavacc(Collection<File> javaSourceFiles) {
        File customAst = getCustomAstClassInputFile(javaSourceFiles);
        if (customAst != null) {
            copyCustomAstClassToTargetDirectory(customAst);
            return true;
        }
        return false;
    }

    public boolean handleCustomAstInJava(Collection<File> javaSourceFiles) {
        File customAst = getCustomAstClassInputFile(javaSourceFiles);
        if (customAst != null) {
            ignoreCompiledFileAndUseCustomAstClassFromJavaSourceTree(customAst);
            return true;
        }
        return false;
    }
}
