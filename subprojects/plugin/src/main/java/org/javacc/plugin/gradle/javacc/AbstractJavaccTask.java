package org.javacc.plugin.gradle.javacc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.SourceTask;
import org.gradle.process.ExecOperations;

public abstract class AbstractJavaccTask extends SourceTask {
    protected Map<String, String> programArguments;
    protected final ExecOperations execOperations;
    protected final List<FileTree> javaSources = new ArrayList<>();

    private File inputDirectory;
    private File outputDirectory;
    private Configuration classpath;

    protected AbstractJavaccTask(String inputDirectory, String outputDirectory, String filter,
                                 ExecOperations execOperations) {
        setInputDirectory(inputDirectory);
        setOutputDirectory(outputDirectory);

        include(filter);
        this.execOperations = execOperations;
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

    @Internal
    protected Configuration getClasspath() {
        return classpath;
    }

    public void addJavaSources(FileTree source) {
        javaSources.add(source);
    }
}
