package ca.coglinc.gradle.plugins.javacc.compiler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileTree;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ca.coglinc.gradle.plugins.javacc.CompileJavaccTask;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

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
            .withJavaCompileTasks(project.getTasks().withType(JavaCompile.class))
            .build();

        FileTree javaSourceTree = configuration.getJavaSourceTree();

        assertThat(javaSourceTree, contains(javaFile.getCanonicalFile()));
    }

    private File addTaskWithSourceFile(Project project, String taskName, String sourceFileName, Class<? extends SourceTask> taskType) throws IOException {
        Map<String, Object> options = new HashMap<String, Object>();
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
            .withJavaCompileTasks(project.getTasks().withType(JavaCompile.class))
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
            .withJavaCompileTasks(project.getTasks().withType(JavaCompile.class))
            .build();

        FileTree javaSourceTree = configuration.getJavaSourceTree();

        assertThat(javaSourceTree, contains(javaFile.getCanonicalFile()));
        assertThat(javaSourceTree, not(contains(outputFile.getCanonicalFile())));
    }

    @Test
    public void getCompleteSourceTreeReturnsSourceWhenNoJavaCompileTask() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(testFolder.getRoot()).build();
        File javaccFile = addTaskWithSourceFile(project, "compileJavacc", "input/TestClass.jj", CompileJavaccTask.class);
        CompilerInputOutputConfiguration configuration = builder
            .withInputDirectory(inputDirectory)
            .withOutputDirectory(outputDirectory)
            .withSource(((SourceTask) project.getTasks().getByName("compileJavacc")).getSource())
            .build();

        FileTree completeSourceTree = configuration.getCompleteSourceTree();

        assertThat(completeSourceTree, contains(javaccFile.getCanonicalFile()));
    }

    @Test
    public void getCompleteSourceTreeReturnsSourceAndJavaSourceWhenProjectHasJavaCompileTasks() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(testFolder.getRoot()).build();
        File javaccFile = addTaskWithSourceFile(project, "compileJavacc", "input/TestClass.jj", CompileJavaccTask.class);
        testFolder.newFolder("inputJava");
        File javaFile = addTaskWithSourceFile(project, "compileJava", "inputJava/MyClass.java", JavaCompile.class);
        CompilerInputOutputConfiguration configuration = builder
            .withInputDirectory(inputDirectory)
            .withOutputDirectory(outputDirectory)
            .withSource(((SourceTask) project.getTasks().getByName("compileJavacc")).getSource())
            .withJavaCompileTasks(project.getTasks().withType(JavaCompile.class))
            .build();

        FileTree completeSourceTree = configuration.getCompleteSourceTree();

        assertThat(completeSourceTree, containsInAnyOrder(javaccFile.getCanonicalFile(), javaFile.getCanonicalFile()));
    }

    @Test
    public void getCompleteSourceTreeExcludesOutputFolder() throws IOException {
        Project project = ProjectBuilder.builder().withProjectDir(testFolder.getRoot()).build();
        File javaccFile = addTaskWithSourceFile(project, "compileJavacc", "input/TestClass.jj", CompileJavaccTask.class);
        File outputFile = addTaskWithSourceFile(project, "compileJavaccGenerated", "output/Generated.java", JavaCompile.class);
        CompilerInputOutputConfiguration configuration = builder
            .withInputDirectory(inputDirectory)
            .withOutputDirectory(outputDirectory)
            .withSource(((SourceTask) project.getTasks().getByName("compileJavacc")).getSource())
            .withJavaCompileTasks(project.getTasks().withType(JavaCompile.class))
            .build();

        FileTree completeSourceTree = configuration.getCompleteSourceTree();

        assertThat(completeSourceTree, contains(javaccFile.getCanonicalFile()));
        assertThat(completeSourceTree, not(contains(outputFile.getCanonicalFile())));
    }

    private static class JavaccConfigurationBuilder {
        private File inputDirectory;
        private File outputDirectory;
        private TaskCollection<JavaCompile> javaCompileTasks;
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

        public JavaccConfigurationBuilder withJavaCompileTasks(TaskCollection<JavaCompile> javaCompileTasks) {
            this.javaCompileTasks = javaCompileTasks;

            return this;
        }

        public JavaccConfigurationBuilder withSource(FileTree source) {
            this.source = source;

            return this;
        }

        public JavaccCompilerInputOutputConfiguration build() {
            return new JavaccCompilerInputOutputConfiguration(inputDirectory, outputDirectory, source, javaCompileTasks);
        }
    }
}
