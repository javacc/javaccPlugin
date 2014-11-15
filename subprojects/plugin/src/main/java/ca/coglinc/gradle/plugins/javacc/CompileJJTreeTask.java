package ca.coglinc.gradle.plugins.javacc;

import java.io.File;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.javacc.jjtree.JJTree;

public class CompileJJTreeTask extends SourceTask {
    public static final String TASK_NAME_VALUE = "compileJJTree";
    public static final String TASK_DESCRIPTION_VALUE = "Compiles JJTree files into JavaCC files";
    public static final String JAVACC_GROUP = "JavaCC";

    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "jjtree";
    private static final String DEFAULT_OUTPUT_FILENAME = "grammar.jj";

    private File inputDirectory = null;
    private File outputDirectory = new File(getProject().getBuildDir().getAbsolutePath() + DEFAULT_OUTPUT_DIRECTORY);
    private String outputFilename = DEFAULT_OUTPUT_FILENAME;

    public CompileJJTreeTask() {
        include("**/*.jjt");
    }

    @TaskAction
    public void executeTask() throws Exception {
        outputDirectory.mkdirs();

        for (File inputFile : getSource().getFiles()) {
            compileToJavacc(inputFile);
        }
    }

    private void compileToJavacc(File inputFile) throws Exception {
        int errorCode = new JJTree().main(new String[]{getJJTreeOutputDirectoryOption(inputFile.getParentFile()),
                                                       getJJTreeOutputFileOption(),
                                                       inputFile.getAbsolutePath()});
        if (errorCode != 0) {
            throw new IllegalStateException("JJTree failed with error code: [" + errorCode + "]");
        }
    }

    private String getJJTreeOutputDirectoryOption(File parentFile) {
        return "-JJTREE_OUTPUT_DIRECTORY=" + outputDirectory.getAbsolutePath() + parentFile.getAbsolutePath().replace(inputDirectory.getAbsolutePath(), "");
    }

    private String getJJTreeOutputFileOption() {
        return "-OUTPUT_FILE=" + outputFilename;
    }

    void setInputDirectory(File inputDirectory) {
        this.inputDirectory = inputDirectory;
        setSource(this.inputDirectory);
    }

    void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    public File getInputDirectory() {
        return inputDirectory;
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return outputDirectory;
    }

    @Input
    public String getOutputFilename() {
        return outputFilename;
    }
}
