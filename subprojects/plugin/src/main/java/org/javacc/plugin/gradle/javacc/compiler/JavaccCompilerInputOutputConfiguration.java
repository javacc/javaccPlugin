package org.javacc.plugin.gradle.javacc.compiler;

import java.io.File;
import java.util.List;

import org.gradle.api.file.FileTree;
import org.gradle.api.specs.Spec;

public class JavaccCompilerInputOutputConfiguration implements CompilerInputOutputConfiguration {
    private final File inputDirectory;
    private final File outputDirectory;
    private final FileTree source;
    private final List<FileTree> javaSources;

    public JavaccCompilerInputOutputConfiguration(File inputDirectory, File outputDirectory, FileTree source, List<FileTree> javaSources) {
        this.inputDirectory = inputDirectory;
        this.outputDirectory = outputDirectory;
        this.source = source;
        this.javaSources = javaSources;
    }

    @Override
    public File getOutputDirectory() {
        return outputDirectory;
    }

    @Override
    public File getInputDirectory() {
        return inputDirectory;
    }

    @Override
    public File getTempOutputDirectory() {
        return new File(getOutputDirectory(), "tmp");
    }

    @Override
    public FileTree getSource() {
        return source;
    }

    @Override
    public FileTree getJavaSourceTree() {
        FileTree javaSourceTree = null;

        for (FileTree excluded : javaSources) {
            if (javaSourceTree == null) {
                javaSourceTree = excluded;
            } else {
                javaSourceTree = javaSourceTree.plus(excluded);
            }
        }

        return excludeOutputDirectory(javaSourceTree);
    }

    private FileTree excludeOutputDirectory(FileTree sourceTree) {
        if (sourceTree == null) {
            return null;
        }

        Spec<File> notInOutputDirectory = file -> !file.getAbsolutePath()
            .contains(getOutputDirectory().getAbsolutePath());

        return sourceTree.filter(notInOutputDirectory).getAsFileTree();
    }
}
