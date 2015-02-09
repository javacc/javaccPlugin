package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.api.tasks.TaskValidationException;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mockito;

public class CompileJjtreeTaskTest {
    private static final String[] GENERATED_FILES = {"JJTreeOutputTest.jj", "HelloTreeConstants.java", "JJTHelloState.java", "Node.java", "SimpleNode.java" };

    private CompileJjTreeTask task;

    @Before
    public void setUp() {
        Project project = ProjectBuilder.builder().build();
        applyJavaccPluginToProject(project);

        task = (CompileJjTreeTask) project.getTasks().findByName(CompileJjTreeTask.TASK_NAME_VALUE);
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
    public void compileJJTreeToJavaccCompilesEachJJTreeInputFileToJavacc() {
        final File inputDirectory = new File(getClass().getResource("/jjtree/input").getFile());
        task.setInputDirectory(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);

        task.execute();

        Assert.assertTrue(outputDirectory.isDirectory());
        Assert.assertEquals(CompileJjtreeTaskTest.GENERATED_FILES.length, outputDirectory.list().length);
        Assert.assertTrue(Arrays.asList(outputDirectory.list()).containsAll(Arrays.asList(CompileJjtreeTaskTest.GENERATED_FILES)));
    }

    @Test
    public void compileJJTreeToJavaccDoesNotGenerateAnythingIfNoInputFiles() {
        testExecuteTaskWithNoInputFiles(new File[0]);
    }

    private void testExecuteTaskWithNoInputFiles(File[] noInputFiles) {
        final File inputDirectory = Mockito.mock(File.class, Answers.RETURNS_MOCKS.get());
        Mockito.when(inputDirectory.listFiles()).thenReturn(noInputFiles);
        Mockito.when(inputDirectory.exists()).thenReturn(Boolean.TRUE);
        Mockito.when(inputDirectory.isDirectory()).thenReturn(Boolean.TRUE);
        task.setInputDirectory(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);

        task.execute();

        Assert.assertFalse(outputDirectory.isDirectory());
    }

    @Test
    public void compileJJTreeToJavaccDoesNotGenerateAnythingIfInputFilesNull() {
        testExecuteTaskWithNoInputFiles(null);
    }

    @Test
    public void compileJJTreeToJavaccCompilesEachJJTreeInputFileToJavaIntoItsPackage() {
        final File inputDirectory = new File(getClass().getResource("/jjtree/inputWithPackages").getFile());
        task.setInputDirectory(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);

        task.execute();

        Assert.assertTrue(outputDirectory.isDirectory());
        Assert.assertEquals(1, outputDirectory.list().length);
        Assert.assertEquals("test", outputDirectory.list()[0]);
        final String[] filesInTestPackageUnderOutputDirectory = outputDirectory.listFiles()[0].list();
        Assert.assertEquals(CompileJjtreeTaskTest.GENERATED_FILES.length, filesInTestPackageUnderOutputDirectory.length);
        Assert.assertTrue(Arrays.asList(filesInTestPackageUnderOutputDirectory).containsAll(Arrays.asList(CompileJjtreeTaskTest.GENERATED_FILES)));
    }

    @Test(expected = TaskExecutionException.class)
    public void compileJJTreeFailsWhenParserGeneratesAnError() {
        final File inputDirectory = new File(getClass().getResource("/jjtree/inputWithErrors").getFile());
        task.setInputDirectory(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);

        task.execute();
    }

    @Test(expected = TaskValidationException.class)
    public void outputDirectoryIsMandatory() {
        final File inputDirectory = new File(getClass().getResource("/jjtree/input").getFile());
        task.setInputDirectory(inputDirectory);
        task.setOutputDirectory((File) null);

        try {
            task.execute();
        } catch (TaskExecutionException e) {
            Assert.assertTrue(e.getCause() instanceof IllegalArgumentException);
            throw e;
        }
    }

    @Test
    public void taskInputsAreInputDirectory() {
        final File inputDirectory = new File(getClass().getResource("/jjtree/input").getFile());
        task.setInputDirectory(inputDirectory);

        task.execute();

        FileCollection inputFiles = task.getInputs().getFiles();
        Assert.assertEquals(1, inputFiles.getFiles().size());
        Assert.assertEquals("JJTreeOutputTest.jjt", ((File) inputFiles.getFiles().toArray()[0]).getName());
    }

    @Test
    public void taskOutputsAreOutputDirectory() {
        final File inputDirectory = new File(getClass().getResource("/jjtree/input").getFile());
        task.setInputDirectory(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);

        task.execute();

        FileCollection outputFiles = task.getOutputs().getFiles();
        Assert.assertEquals(1, outputFiles.getFiles().size());
        Assert.assertEquals("output", ((File) outputFiles.getFiles().toArray()[0]).getName());
    }
}
