package org.javacc.plugin.gradle.javacc.compiler;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class JavaccCompilerInputOutputConfigurationTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private JavaccConfigurationBuilder builder;
    private File inputDirectory;
    private File outputDirectory;

    @Before
    public void setUp() throws Exception {
        builder = JavaccConfigurationBuilder.builder();
        inputDirectory = testFolder.newFolder("input");
        outputDirectory = testFolder.newFolder("output");
    }

    @Test
    public void getTempOutputDirectoryAppendsTmpToOutputDirectory() throws IOException {
        CompilerInputOutputConfiguration configuration = builder
            .withInputDirectory(inputDirectory)
            .withOutputDirectory(outputDirectory)
            .build();

        File tempOutputDirectory = configuration.getTempOutputDirectory();

        assertThat(tempOutputDirectory.getAbsolutePath(), is(equalTo(outputDirectory.getAbsolutePath() + File.separator + "tmp")));
    }

    @Test
    public void getJavaSourceTreeReturnsNullWhenNoJavaCompileTasks() throws IOException {
        CompilerInputOutputConfiguration configuration = builder
            .withInputDirectory(inputDirectory)
            .withOutputDirectory(outputDirectory)
            .build();

        FileTree javaSourceTree = configuration.getJavaSourceTree();

        assertThat(javaSourceTree, is(nullValue()));
    }

    @Test
    public void getJavaSourceTreeReturnsJavaCompileSourceWhenSingleJavaCompileTask() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(testFolder.getRoot()).build();
        File javaFile = addTaskWithSourceFile(project, "compileJava", "input/TestClass.java", JavaCompile.class);
        CompilerInputOutputConfiguration configuration = builder
            .withInputDirectory(inputDirectory)
            .withOutputDirectory(outputDirectory)
            .withJavaSources(getJavaSources(project))
            .build();

        FileTree javaSourceTree = configuration.getJavaSourceTree();

        assertThat(javaSourceTree, contains(javaFile.getCanonicalFile()));
    }

    private List<FileTree> getJavaSources(Project project) {
        return project.getTasks().withType(JavaCompile.class)
            .stream().map(JavaCompile::getSource).collect(Collectors.toList());
    }

    private File addTaskWithSourceFile(Project project, String taskName, String sourceFileName, Class<? extends SourceTask> taskType) throws IOException {
        Map<String, Object> options = new HashMap<>();
        options.put(Task.TASK_TYPE, taskType);

        SourceTask compileJava = (SourceTask) project.task(options, taskName);
        File javaFile = testFolder.newFile(sourceFileName);
        compileJava.source(javaFile.getCanonicalFile());

        return javaFile;
    }

    @Test
    public void getJavaSourceTreeReturnsAggregatedJavaCompileSourceWhenMultipleJavaCompileTasks() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(testFolder.getRoot()).build();
        File javaFile = addTaskWithSourceFile(project, "compileJava", "input/TestClass.java", JavaCompile.class);
        testFolder.newFolder("inputTest");
        File testFile = addTaskWithSourceFile(project, "compileTest", "inputTest/AnotherTestClass.java", JavaCompile.class);
        CompilerInputOutputConfiguration configuration = builder
            .withInputDirectory(inputDirectory)
            .withOutputDirectory(outputDirectory)
            .withJavaSources(getJavaSources(project))
            .build();

        FileTree javaSourceTree = configuration.getJavaSourceTree();

        assertThat(javaSourceTree, containsInAnyOrder(javaFile.getCanonicalFile(), testFile.getCanonicalFile()));
    }

    @Test
    public void getJavaSourceTreeExcludesOutputFolder() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(testFolder.getRoot()).build();
        File javaFile = addTaskWithSourceFile(project, "compileJava", "input/TestClass.java", JavaCompile.class);
        File outputFile = addTaskWithSourceFile(project, "compileJavaccGenerated", "output/Generated.java", JavaCompile.class);
        CompilerInputOutputConfiguration configuration = builder
            .withInputDirectory(inputDirectory)
            .withOutputDirectory(outputDirectory)
            .withJavaSources(getJavaSources(project))
            .build();

        FileTree javaSourceTree = configuration.getJavaSourceTree();

        assertThat(javaSourceTree, contains(javaFile.getCanonicalFile()));
        assertThat(javaSourceTree, not(contains(outputFile.getCanonicalFile())));
    }


    private static class JavaccConfigurationBuilder {
        private File inputDirectory;
        private File outputDirectory;
        private List<FileTree> javaSourceTrees = new ArrayList<>();
        private FileTree source;

        private JavaccConfigurationBuilder() {
        }

        public static JavaccConfigurationBuilder builder() {
            return new JavaccConfigurationBuilder();
        }

        public JavaccConfigurationBuilder withInputDirectory(File inputDirectory) {
            this.inputDirectory = inputDirectory;

            return this;
        }

        public JavaccConfigurationBuilder withOutputDirectory(File outputDirectory) {
            this.outputDirectory = outputDirectory;

            return this;
        }

        public JavaccConfigurationBuilder withJavaSources(List<FileTree> javaSourceTrees) {
            this.javaSourceTrees.addAll(javaSourceTrees);

            return this;
        }

        public JavaccCompilerInputOutputConfiguration build() {
            return new JavaccCompilerInputOutputConfiguration(inputDirectory, outputDirectory, source, javaSourceTrees);
        }
    }
}
