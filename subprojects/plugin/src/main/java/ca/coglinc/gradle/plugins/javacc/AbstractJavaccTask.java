package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.javacc.parser.Main;

public abstract class AbstractJavaccTask extends SourceTask {

    private File inputDirectory;
    private File outputDirectory;
    private Map<String, String> programArguments;

    protected AbstractJavaccTask(String inputDirectory, String outputDirectory, String filter) {
        setInputDirectory(inputDirectory);
        setOutputDirectory(outputDirectory);

        include(filter);
    }

    @TaskAction
    public void run() throws Exception {
        getOutputDirectory().mkdirs();

        for (File inputFile : getSource().getFiles()) {
            compile(inputFile);
        }
    }

    protected void compile(File inputFile) throws Exception {
        getLogger().debug("Compiling {} file [{}] to [{}]", getProgramName(), inputFile.getAbsolutePath(), getOutputDirectory().getAbsolutePath());

        String[] arguments = buildProgramArguments(inputFile);

        getLogger().debug("Invoking {} with arguments [{}]", getProgramName(), arguments);
        invokeCompiler(arguments);
    }

    protected abstract void invokeCompiler(String[] arguments) throws Exception;

    public Map<String, String> getArguments() {
        return programArguments;
    }

    public AbstractJavaccTask arguments(Map<String, String> arguments) {
        return setArguments(arguments);
    }

    public AbstractJavaccTask setArguments(Map<String, String> arguments) {
        this.programArguments = arguments;

        return this;
    }

    public File getInputDirectory() {
        return inputDirectory;
    }

    @OutputDirectory
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public AbstractJavaccTask inputDirectory(String inputDirectory) {
        return setInputDirectory(inputDirectory);
    }

    public AbstractJavaccTask inputDirectory(File inputDirectory) {
        return setInputDirectory(inputDirectory);
    }

    public AbstractJavaccTask outputDirectory(String outputDirectory) {
        return setOutputDirectory(outputDirectory);
    }

    public AbstractJavaccTask outputDirectory(File outputDirectory) {
        return setOutputDirectory(outputDirectory);
    }

    public AbstractJavaccTask setInputDirectory(String inputDirectory) {
        return setInputDirectory(new File(getProject().getProjectDir(), inputDirectory));
    }

    public AbstractJavaccTask setInputDirectory(File inputDirectory) {
        getLogger().debug("Changing input directory to [{}]", inputDirectory);

        this.inputDirectory = inputDirectory;
        setSource(inputDirectory);

        return this;
    }

    public AbstractJavaccTask setOutputDirectory(String outputDirectory) {
        return setOutputDirectory(new File(getProject().getBuildDir(), outputDirectory));
    }

    public AbstractJavaccTask setOutputDirectory(File outputDirectory) {
        getLogger().debug("Changing output directory to [{}]", outputDirectory);

        this.outputDirectory = outputDirectory;

        return this;
    }

    String[] buildProgramArguments(File inputFile) {
        Map<String, String> arguments = new HashMap<String, String>();
        if (programArguments != null)
            arguments.putAll(programArguments);

        augmentArguments(inputFile, arguments);

        int index = 0;
        String[] commandLineArguments = new String[arguments.size() + 1];

        // Add normal arguments
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            commandLineArguments[index++] = String.format("-%1$s=%2$s", entry.getKey(), entry.getValue());
        }

        // Add file to compile as last command line argument
        commandLineArguments[commandLineArguments.length - 1] = inputFile.getAbsolutePath();

        return commandLineArguments;
    }

    /**
     * Gives a chance to sub-classes to add some required arguments for example, the output directory.
     *
     * @param inputFile
     *            The input file for each arguments should be augmented. This is the file that will be "compiled".
     * @param arguments
     *            The map to add new arguments to.
     */
    protected abstract void augmentArguments(File inputFile, Map<String, String> arguments);

    protected File computeInputFileOutputDirectory(File inputFile) {
        File inputParentFile = inputFile.getParentFile();

        // FIXME: This is not correct, multiple sources could be added to the `SourceTask`.
        // Implications of having multiple sources is that `getInputDirectory()` is not correct
        // to replace to "" in all cases.
        return new File(getOutputDirectory(), inputParentFile.getAbsolutePath().replace(getInputDirectory().getAbsolutePath(), ""));
    }

    protected abstract String getProgramName();
}
