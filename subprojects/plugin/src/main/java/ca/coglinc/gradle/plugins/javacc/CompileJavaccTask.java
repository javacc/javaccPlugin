package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.file.RelativePath;
import org.gradle.api.tasks.TaskAction;
import org.javacc.parser.Main;

public class CompileJavaccTask extends AbstractJavaccTask {
    public static final String TASK_NAME_VALUE = "compileJavacc";
    public static final String TASK_DESCRIPTION_VALUE = "Compiles JavaCC files into Java files";

    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "javacc";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "javacc";
    private static final String SUPPORTED_FILE_SUFFIX = ".jj";

    public CompileJavaccTask() {
        super(CompileJavaccTask.DEFAULT_INPUT_DIRECTORY, CompileJavaccTask.DEFAULT_OUTPUT_DIRECTORY, "**/*" + SUPPORTED_FILE_SUFFIX);
    }

    @TaskAction
    public void run() {
        getTempOutputDirectory().mkdirs();

        compileSourceFilesToTempOutputDirectory();
        copyCompiledFilesFromTempOutputDirectoryToOutputDirectory();
        copyNonJavaccFilesToOutputDirectory();
        FileUtils.deleteQuietly(getTempOutputDirectory());
    }

    private void copyNonJavaccFilesToOutputDirectory() {
        getSource().visit(getNonJavaccSourceFileVisitor());
    }

    private File getTempOutputDirectory() {
        return new File(getOutputDirectory(), "tmp");
    }

    private void compileSourceFilesToTempOutputDirectory() {
        getSource().visit(getJavaccSourceFileVisitor());
    }

    private void copyCompiledFilesFromTempOutputDirectoryToOutputDirectory() {
        Collection<File> tempCompiledFiles = FileUtils.listFiles(getTempOutputDirectory(), TrueFileFilter.TRUE, TrueFileFilter.TRUE);
        for (File tempCompiledFile : tempCompiledFiles) {
            File customAstClassInputFile = new File(tempCompiledFile.getAbsolutePath().replace(getTempOutputDirectory().getAbsolutePath(),
                getInputDirectory().getAbsolutePath()));
            String compiledFileOutputPath = tempCompiledFile.getAbsolutePath().replace(getTempOutputDirectory().getAbsolutePath(),
                getOutputDirectory().getAbsolutePath());
            File compiledFileOutputFile = new File(compiledFileOutputPath);

            if (!customAstClassInputFile.exists()) {
                copyGeneratedFilesToOutputDirectory(tempCompiledFile, customAstClassInputFile, compiledFileOutputFile);
            } else {
                copyCustomAstClassToOutputDirectory(tempCompiledFile, customAstClassInputFile, compiledFileOutputPath, compiledFileOutputFile);
            }
        }
    }

    private void copyGeneratedFilesToOutputDirectory(File tempCompiledFile, File customAstClassInputFile, File compiledFileOutputFile) {
        getLogger().debug("Custom AST class {} not found", customAstClassInputFile.getAbsolutePath());
        getLogger().debug("Copying compiled file {} from {} to {}", tempCompiledFile.getAbsolutePath(), getTempOutputDirectory().getAbsolutePath(),
            getOutputDirectory().getAbsolutePath());
        try {
            FileUtils.moveFile(tempCompiledFile, compiledFileOutputFile);
        } catch (IOException e) {
            throw new JavaccTaskException(String.format("Could not copy %s from %s to %s", tempCompiledFile.getAbsolutePath(),
                getTempOutputDirectory().getAbsolutePath(), getOutputDirectory().getAbsolutePath()), e);
        }
    }

    private void copyCustomAstClassToOutputDirectory(File tempCompiledFile, File customAstClassInputFile, String compiledFileOutputPath,
        File compiledFileOutputFile) {
        getLogger().debug("Not copying compiled file {} from {} to {} because it is overridden by the custom AST class {}",
            tempCompiledFile.getAbsolutePath(), getTempOutputDirectory().getAbsolutePath(), getOutputDirectory().getAbsolutePath(),
            customAstClassInputFile.getAbsolutePath());
        try {
            FileUtils.copyFile(customAstClassInputFile, compiledFileOutputFile);
        } catch (IOException e) {
            throw new JavaccTaskException(String.format("Could not copy %s from %s", customAstClassInputFile.getAbsolutePath(),
                compiledFileOutputPath), e);
        }
    }

    @Override
    protected void augmentArguments(File inputDirectory, RelativePath inputRelativePath, Map<String, String> arguments) {
        arguments.put("OUTPUT_DIRECTORY", inputRelativePath.getFile(getTempOutputDirectory()).getParentFile().getAbsolutePath());
    }

    @Override
    protected String getProgramName() {
        return "JavaCC";
    }

    @Override
    protected void invokeCompiler(String[] arguments) throws Exception {
        int errorCode = Main.mainProgram(arguments);
        if (errorCode != 0) {
            throw new IllegalStateException("Javacc failed with error code: [" + errorCode + "]");
        }
    }

    @Override
    protected FileVisitor getJavaccSourceFileVisitor() {
        return new JavaccSourceFileVisitor(this);
    }

    @Override
    protected String supportedSuffix() {
        return SUPPORTED_FILE_SUFFIX;
    }
}
