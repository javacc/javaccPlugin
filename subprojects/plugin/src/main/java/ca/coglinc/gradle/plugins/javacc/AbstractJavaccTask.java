package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.util.Map;

import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.file.RelativePath;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;

import ca.coglinc.gradle.plugins.javacc.compilationresults.CompiledJavaccFile;
import ca.coglinc.gradle.plugins.javacc.compilationresults.CompiledJavaccFilesDirectory;
import ca.coglinc.gradle.plugins.javacc.compilationresults.CompiledJavaccFilesDirectoryFactory;

public abstract class AbstractJavaccTask extends SourceTask {
    protected Map<String, String> programArguments;

    private File inputDirectory;
    private File outputDirectory;
    private CompiledJavaccFilesDirectoryFactory compiledJavaccFilesDirectoryFactory = new CompiledJavaccFilesDirectoryFactory();

    protected AbstractJavaccTask(String inputDirectory, String outputDirectory, String filter) {
        setInputDirectory(inputDirectory);
        setOutputDirectory(outputDirectory);

        include(filter);
    }

    protected void compile(File inputDirectory, RelativePath inputRelativePath) {
        getLogger().debug("Compiling {} file [{}] from [{}] into [{}]", getProgramName(), inputRelativePath, inputDirectory, getOutputDirectory());

        ProgramArguments arguments = buildProgramArguments(inputDirectory, inputRelativePath);

        getLogger().debug("Invoking {} with arguments [{}]", getProgramName(), arguments);
        try {
            invokeCompiler(arguments);
        } catch (Exception exception) {
            final String errorMessage = String.format("Unable to compile '%s' from '%s' into '%s'", inputRelativePath, inputDirectory, getOutputDirectory());
            throw new JavaccTaskException(errorMessage, exception);
        }
    }

    protected abstract void invokeCompiler(ProgramArguments arguments) throws Exception;

    protected void copyNonJavaccFilesToOutputDirectory() {
        getSource().visit(getNonJavaccSourceFileVisitor());
    }

    @Internal
    protected File getTempOutputDirectory() {
        return new File(getOutputDirectory(), "tmp");
    }

    protected void compileSourceFilesToTempOutputDirectory() {
        getSource().visit(getJavaccSourceFileVisitor());
    }

    protected void copyCompiledFilesFromTempOutputDirectoryToOutputDirectory() {
        CompiledJavaccFilesDirectory compiledJavaccFilesDirectory
            = compiledJavaccFilesDirectoryFactory.getCompiledJavaccFilesDirectory(getTempOutputDirectory(), getCompleteSourceTree(), getOutputDirectory(), getLogger());

        for (CompiledJavaccFile compiledJavaccFile : compiledJavaccFilesDirectory.listFiles()) {
            FileTree javaSourceTree = getJavaSourceTree();
            if (compiledJavaccFile.customAstClassExists(javaSourceTree)) {
                compiledJavaccFile.ignoreCompiledFileAndUseCustomAstClassFromJavaSourceTree(javaSourceTree);
            } else if (compiledJavaccFile.customAstClassExists()) {
                compiledJavaccFile.copyCustomAstClassToTargetDirectory(getCompleteSourceTree());
            } else {
                compiledJavaccFile.copyCompiledFileToTargetDirectory();
            }
        }
    }

    @Internal
    private FileTree getCompleteSourceTree() {
        FileTree javaccTaskSourceTree = getSource();
        FileTree javaTasksSourceTree = getJavaSourceTree();
        FileTree completeSourceTree = null;

        if (javaTasksSourceTree == null) {
            completeSourceTree = javaccTaskSourceTree;
        } else {
            completeSourceTree = javaccTaskSourceTree.plus(javaTasksSourceTree);
        }

        return excludeOutputDirectory(completeSourceTree);
    }

    private FileTree excludeOutputDirectory(FileTree sourceTree) {
        if (sourceTree == null) {
            return null;
        }

        Spec<File> outputDirectoryFilter = new Spec<File>() {

            @Override
            public boolean isSatisfiedBy(File file) {
                return file.getAbsolutePath().contains(getOutputDirectory().getAbsolutePath());
            }
        };

        sourceTree = sourceTree.minus(sourceTree.filter(outputDirectoryFilter)).getAsFileTree();
        return sourceTree;
    }

    @Internal
    private FileTree getJavaSourceTree() {
        FileTree javaSourceTree = null;
        TaskCollection<JavaCompile> javaCompileTasks = this.getProject().getTasks().withType(JavaCompile.class);

        for (JavaCompile task : javaCompileTasks) {
            if (javaSourceTree == null) {
                javaSourceTree = task.getSource();
            } else {
                javaSourceTree = javaSourceTree.plus(task.getSource());
            }
        }

        return excludeOutputDirectory(javaSourceTree);
    }

    @Internal
    protected abstract FileVisitor getJavaccSourceFileVisitor();

    @Internal
    protected FileVisitor getNonJavaccSourceFileVisitor() {
        return new NonJavaccSourceFileVisitor(this);
    }

    @Internal
    public Map<String, String> getArguments() {
        return programArguments;
    }

    public AbstractJavaccTask setArguments(Map<String, String> arguments) {
        this.programArguments = arguments;

        return this;
    }

    @InputDirectory
    @SkipWhenEmpty
    @PathSensitive(PathSensitivity.NONE)
    @Optional
    public File getInputDirectory() {
        if (!inputDirectory.exists()) {
            return null;
        } else {
            return inputDirectory;
        }
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

    ProgramArguments buildProgramArguments(File inputDirectory, RelativePath inputRelativePath) {
        ProgramArguments commandLineArguments = new ProgramArguments();

        if (programArguments != null) {
            commandLineArguments.addAll(programArguments);
        }

        commandLineArguments.addFilename(inputRelativePath.getFile(inputDirectory).getAbsolutePath());

        augmentArguments(inputDirectory, inputRelativePath, commandLineArguments);

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
     *            The ProgramArguments to add new arguments to.
     */
    protected abstract void augmentArguments(File inputDirectory, RelativePath inputRelativePath, ProgramArguments arguments);

    @Internal
    protected abstract String getProgramName();

    protected abstract String supportedSuffix();
}
