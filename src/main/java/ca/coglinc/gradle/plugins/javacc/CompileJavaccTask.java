package ca.coglinc.gradle.plugins.javacc;

import java.io.File;

import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.javacc.parser.Main;

public class CompileJavaccTask extends SourceTask {
    public static final String TASK_NAME_VALUE = "compileJavacc";
    public static final String TASK_DESCRIPTION_VALUE = "Compiles javacc files into java files";
    public static final String JAVACC_GROUP = "JavaCC";

    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "javacc";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "javacc";

    private File inputDirectory = new File(getProject().getRootDir().getAbsolutePath() + DEFAULT_INPUT_DIRECTORY);
    private File outputDirectory = new File(getProject().getBuildDir().getAbsolutePath() + DEFAULT_OUTPUT_DIRECTORY);

    public CompileJavaccTask() {
        setSource(inputDirectory);
        include("**/*.jj");
    }

    @TaskAction
    public void executeTask() throws Exception {
        outputDirectory.mkdirs();

        for (File inputFile : getSource().getFiles()) {
            compileToJava(inputFile);
        }
    }

    private void compileToJava(File inputFile) throws Exception {
        int errorCode = Main.mainProgram(new String[]{getJavaccOutputDirectoryOption(inputFile.getParentFile()), inputFile.getAbsolutePath()});
        if (errorCode != 0) {
            throw new IllegalStateException("Javacc failed with error code: [" + errorCode + "]");
        }
    }

    private String getJavaccOutputDirectoryOption(File parentFile) {
        return "-OUTPUT_DIRECTORY=" + outputDirectory.getAbsolutePath() + parentFile.getAbsolutePath().replace(inputDirectory.getAbsolutePath(), "");
    }

    void setInputDirectory(File inputDirectory) {
        this.inputDirectory = inputDirectory;
        setSource(this.inputDirectory);
    }

    void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public File getInputDirectory() {
        return inputDirectory;
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return outputDirectory;
    }
}
