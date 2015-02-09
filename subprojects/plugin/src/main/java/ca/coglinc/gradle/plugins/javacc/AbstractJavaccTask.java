package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.file.EmptyFileVisitor;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.RelativePath;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;

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
    public void run() {
        getOutputDirectory().mkdirs();

        getSource().visit(new EmptyFileVisitor() {
            @Override
            public void visitFile(FileVisitDetails fileVisitDetails) {
                compile(computeInputDirectory(fileVisitDetails), fileVisitDetails.getRelativePath());
            }
        });
    }

    protected void compile(File inputDirectory, RelativePath inputRelativePath) {
        getLogger().debug("Compiling {} file [{}] from [{}] into [{}]", getProgramName(), inputRelativePath, inputDirectory, getOutputDirectory());

        String[] arguments = buildProgramArguments(inputDirectory, inputRelativePath);

        getLogger().debug("Invoking {} with arguments [{}]", getProgramName(), arguments);
        try {
            invokeCompiler(arguments);
        } catch (Exception exception) {
            final String errorMessage = String.format("Unable to compile '%s' from '%s' into '%s'", inputRelativePath, inputDirectory,
                getOutputDirectory());
            throw new JavaccTaskException(errorMessage, exception);
        }
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

    String[] buildProgramArguments(File inputDirectory, RelativePath inputRelativePath) {
        Map<String, String> arguments = new HashMap<String, String>();
        if (programArguments != null) {
            arguments.putAll(programArguments);
        }

        augmentArguments(inputDirectory, inputRelativePath, arguments);

        int index = 0;
        String[] commandLineArguments = new String[arguments.size() + 1];

        // Add normal arguments
        for (Map.Entry<String, String> entry : arguments.entrySet()) {
            commandLineArguments[index++] = String.format("-%1$s=%2$s", entry.getKey(), entry.getValue());
        }

        // Add file to compile as last command line argument
        commandLineArguments[commandLineArguments.length - 1] = inputRelativePath.getFile(inputDirectory).getAbsolutePath();

        return commandLineArguments;
    }

    /**
     * Gives a chance to sub-classes to add some required arguments for example, the output directory.
     *
     * @param inputDirectory
     *            The input directory from which input relative path is derived.
     * @param inputRelativePath
     *            The input path relative to the input directory. This is the file that will be "compiled".
     * @param arguments
     *            The map to add new arguments to.
     */
    protected abstract void augmentArguments(File inputDirectory, RelativePath inputRelativePath, Map<String, String> arguments);

    protected abstract String getProgramName();

    private File computeInputDirectory(FileVisitDetails fileVisitDetails) {
        File fileAbsolute = fileVisitDetails.getFile();
        File fileRelative = new File(fileVisitDetails.getPath());

        return new File(fileAbsolute.getAbsolutePath().replace(fileRelative.getPath(), ""));
    }
}
