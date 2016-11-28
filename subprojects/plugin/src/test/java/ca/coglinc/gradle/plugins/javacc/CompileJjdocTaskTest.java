package ca.coglinc.gradle.plugins.javacc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;

public class CompileJjdocTaskTest {
    private static final String[] GENERATED_FILES = {"MyParser.html", "AnotherParser.html" };

    private CompileJjdocTask task;

    @Before
    public void setUp() {
        Project project = ProjectBuilder.builder().build();
        project.getRepositories().jcenter();
        applyJavaccPluginToProject(project);

        task = (CompileJjdocTask) project.getTasks().findByName(CompileJjdocTask.TASK_NAME_VALUE);
    }

    private void applyJavaccPluginToProject(Project project) {
        Map<String, String> pluginNames = new HashMap<String, String>(1);
        pluginNames.put("plugin", "ca.coglinc.javacc");

        project.apply(pluginNames);
    }

    @After
    public void tearDown() {
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        if (outputDirectory.exists()) {
            deleteDirectories(outputDirectory);
        }
    }

    private void deleteDirectories(File outputDirectory) {
        for (File outputFile : outputDirectory.listFiles()) {
            if (outputFile.isDirectory()) {
                deleteDirectories(outputFile);
            } else {
                outputFile.delete();
            }
        }
        outputDirectory.delete();
    }

    @Test
    public void supportsDotJjFiles() {
        String supportedSuffix = task.supportedSuffix();

        assertEquals(".jj", supportedSuffix);
    }

    @Test
    public void programNameIsJjdoc() {
        String programName = task.getProgramName();

        assertEquals("JJDoc", programName);
    }

    @Test
    public void getFileVisitorReturnsInstanceOfJavaccSourceFileVisitor() {
        FileVisitor sourceFileVisitor = task.getJavaccSourceFileVisitor();

        assertTrue(sourceFileVisitor instanceof JavaccSourceFileVisitor);
    }

    @Test
    public void jjdocDoesNotGenerateAnythingIfNoInputFiles() {
        testExecuteTaskWithNoInputFiles(new File[0]);
    }

    private void testExecuteTaskWithNoInputFiles(File[] noInputFiles) {
        final File inputDirectory = Mockito.mock(File.class, Answers.RETURNS_MOCKS.get());
        Mockito.when(inputDirectory.listFiles()).thenReturn(noInputFiles);
        Mockito.when(inputDirectory.exists()).thenReturn(Boolean.TRUE);
        Mockito.when(inputDirectory.isDirectory()).thenReturn(Boolean.TRUE);
        task.setSource(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);

        task.execute();

        assertFalse(outputDirectory.isDirectory());
    }

    @Test
    public void jjdocDoesNotGenerateAnythingIfInputFilesNull() {
        testExecuteTaskWithNoInputFiles(null);
    }

    @Test
    public void compileJavaccToJavaCompilesEachJavaccInputFileToJava() {
        setTaskInputDirectory("/jjdoc/input");
        final File outputDirectory = setTaskOutputDirectory("output");

        task.execute();

        assertTrue(outputDirectory.isDirectory());
        assertEquals(CompileJjdocTaskTest.GENERATED_FILES.length, outputDirectory.list().length);
        assertTrue(Arrays.asList(outputDirectory.list()).containsAll(Arrays.asList(CompileJjdocTaskTest.GENERATED_FILES)));
    }

    private File setTaskInputDirectory(final String inputDirectoryName) {
        final File inputDirectory = new File(getClass().getResource(inputDirectoryName).getFile());
        task.setInputDirectory(inputDirectory);

        return inputDirectory;
    }

    private File setTaskOutputDirectory(final String outputDirectoryName) {
        final File outputDirectory = new File(getClass().getResource("/").getFile() + outputDirectoryName);
        task.setOutputDirectory(outputDirectory);
        return outputDirectory;
    }

    @Test(expected = TaskExecutionException.class)
    public void compileJavaccFailsWhenParserGeneratesAnError() {
        setTaskInputDirectory("/jjdoc/inputWithErrors");
        setTaskOutputDirectory("output");

        task.execute();
    }
}
