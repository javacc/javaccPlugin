package org.javacc.plugin.gradle.javacc.compiler;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.file.RelativePath;
import org.gradle.api.logging.Logger;

import org.javacc.plugin.gradle.javacc.JavaccTaskException;
import org.javacc.plugin.gradle.javacc.compilationresults.CompiledJavaccFile;
import org.javacc.plugin.gradle.javacc.compilationresults.CompiledJavaccFilesDirectory;
import org.javacc.plugin.gradle.javacc.compilationresults.CompiledJavaccFilesDirectoryFactory;
import org.javacc.plugin.gradle.javacc.programexecution.ProgramArguments;
import org.javacc.plugin.gradle.javacc.programexecution.ProgramInvoker;

/**
 * This {@link SourceFileCompiler} implementation invokes JavaCC programs to compile supported files to Java or JavaCC files.
 *
 * It supports the following programs:
 * <ul>
 *     <li>JavaCC</li>
 *     <li>JJTree</li>
 *     <li>JJDoc</li>
 * </ul>
 */
public class JavaccSourceFileCompiler implements SourceFileCompiler {
    private final ProgramInvoker programInvoker;
    private final ProgramArguments argumentsProvidedByTask;
    private final CompilerInputOutputConfiguration configuration;
    private CompiledJavaccFilesDirectoryFactory compiledJavaccFilesDirectoryFactory = new CompiledJavaccFilesDirectoryFactory();
    private final Logger logger;

    public JavaccSourceFileCompiler(ProgramInvoker programInvoker, ProgramArguments argumentsProvidedByTask, CompilerInputOutputConfiguration configuration, Logger logger) {
        this.programInvoker = programInvoker;
        this.argumentsProvidedByTask = argumentsProvidedByTask;
        this.configuration = configuration;
        this.logger = logger;
    }

    @Override
    public void compileSourceFilesToTempOutputDirectory() {
        configuration.getSource().visit(getJavaccSourceFileVisitor());
    }

    private FileVisitor getJavaccSourceFileVisitor() {
        return new JavaccSourceFileVisitor(this);
    }

    @Override
    public void copyNonJavaccFilesToOutputDirectory() {
        configuration.getSource().visit(getNonJavaccSourceFileVisitor());
    }

    private FileVisitor getNonJavaccSourceFileVisitor() {
        return new NonJavaccSourceFileVisitor(this);
    }

    @Override
    public void compile(File inputDirectory, RelativePath inputRelativePath) {
        logger.debug("Compiling {} file [{}] from [{}] into [{}]", programInvoker.getProgramName(), inputRelativePath, inputDirectory, getOutputDirectory());

        ProgramArguments arguments = buildProgramArguments(inputDirectory, inputRelativePath);

        logger.debug("Invoking {} with arguments [{}]", programInvoker.getProgramName(), arguments);
        try {
            programInvoker.invokeCompiler(arguments);
        } catch (Exception exception) {
            final String errorMessage = String.format("Unable to compile '%s' from '%s' into '%s'", inputRelativePath, inputDirectory, getOutputDirectory());
            throw new JavaccTaskException(errorMessage, exception);
        }
    }

    private ProgramArguments buildProgramArguments(File inputDirectory, RelativePath inputRelativePath) {
        ProgramArguments commandLineArguments = new ProgramArguments(argumentsProvidedByTask);
        commandLineArguments.addFilename(inputRelativePath.getFile(inputDirectory).getAbsolutePath());
        return programInvoker.augmentArguments(inputDirectory, inputRelativePath, commandLineArguments);
    }

    @Override
    public void copyCompiledFilesFromTempOutputDirectoryToOutputDirectory() {
        CompiledJavaccFilesDirectory compiledJavaccFilesDirectory = compiledJavaccFilesDirectoryFactory.getCompiledJavaccFilesDirectory(
            configuration.getTempOutputDirectory(), configuration.getCompleteSourceTree(), getOutputDirectory(), getLogger());

        for (CompiledJavaccFile compiledJavaccFile : compiledJavaccFilesDirectory.listFiles()) {
            FileTree javaSourceTree = configuration.getJavaSourceTree();
            if (compiledJavaccFile.customAstClassExists(javaSourceTree)) {
                compiledJavaccFile.ignoreCompiledFileAndUseCustomAstClassFromJavaSourceTree(javaSourceTree);
            } else if (compiledJavaccFile.customAstClassExists()) {
                compiledJavaccFile.copyCustomAstClassToTargetDirectory(configuration.getCompleteSourceTree());
            } else {
                compiledJavaccFile.copyCompiledFileToTargetDirectory();
            }
        }
    }

    @Override
    public String supportedSuffix() {
        return programInvoker.supportedSuffix();
    }

    @Override
    public String getProgramName() {
        return programInvoker.getProgramName();
    }

    @Override
    public File getOutputDirectory() {
        return configuration.getOutputDirectory();
    }

    @Override
    public File getInputDirectory() {
        return configuration.getInputDirectory();
    }

    @Override
    public void createTempOutputDirectory() {
        configuration.getTempOutputDirectory().mkdirs();
    }

    @Override
    public void cleanTempOutputDirectory() {
        FileUtils.deleteQuietly(configuration.getTempOutputDirectory());
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    void setCompiledJavaccFilesDirectoryFactoryForTest(CompiledJavaccFilesDirectoryFactory factory) {
        compiledJavaccFilesDirectoryFactory = factory;
    }
}
