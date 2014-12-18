package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.util.Map;

import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.javacc.jjtree.JJTree;

public class CompileJJTreeTask extends SourceTask {
    public static final String TASK_NAME_VALUE = "compileJJTree";
    public static final String TASK_DESCRIPTION_VALUE = "Compiles JJTree files into JavaCC files";

    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "jjtree";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "jjtree";

    private File inputDirectory = new File(getProject().getProjectDir().getAbsolutePath() + DEFAULT_INPUT_DIRECTORY);
    private File outputDirectory = new File(getProject().getBuildDir().getAbsolutePath() + DEFAULT_OUTPUT_DIRECTORY);
    private Map<String, String> jjtreeArguments;

    public CompileJJTreeTask() {
        include("**/*.jjt");

        setInputDirectory(inputDirectory);
    }

    @TaskAction
    public void executeTask() throws Exception {
        outputDirectory.mkdirs();

        for (File inputFile : getSource().getFiles()) {
            compileToJavacc(inputFile);
        }
    }

    private void compileToJavacc(File inputFile) throws Exception {
        getLogger().debug("Compiling JJTree file [{}] to [{}]", inputFile.getAbsolutePath(), outputDirectory.getAbsolutePath());

        String[] arguments = getJJTreeArgumentsForCommandLine(inputFile);
        getLogger().debug("Invoking JJTree with arguments [{}]", (Object[]) arguments);

        int errorCode = new JJTree().main(arguments);
        if (errorCode != 0) throw new IllegalStateException("JJTree failed with error code: [" + errorCode + "]");
    }

    String[] getJJTreeArgumentsForCommandLine(File inputFile) {
        String[] argumentsForCommandLine = JavaccHelper.prepareArguments(jjtreeArguments);

        argumentsForCommandLine[0] = getJJTreeOutputDirectoryOption(inputFile.getParentFile());
        argumentsForCommandLine[argumentsForCommandLine.length - 1] = inputFile.getAbsolutePath();

        return argumentsForCommandLine;
    }

    private String getJJTreeOutputDirectoryOption(File parentFile) {
        return "-JJTREE_OUTPUT_DIRECTORY=" + outputDirectory.getAbsolutePath() + parentFile.getAbsolutePath().replace(inputDirectory.getAbsolutePath(), "");
    }

    void setInputDirectory(File inputDirectory) {
        getLogger().debug("Changing source folder to [{}]", inputDirectory);

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

    public Map<String, String> getArguments() {
        return jjtreeArguments;
    }

    public void setArguments(Map<String, String> arguments) {
        this.jjtreeArguments = arguments;
    }
}
