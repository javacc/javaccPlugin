package ca.coglinc.gradle.plugins.javacc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.api.tasks.TaskValidationException;
import org.gradle.testfixtures.ProjectBuilder;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsArrayContainingInOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;

public class CompileJavaccTaskTest {
    private static final String[] GENERATED_FILES = {"JavaccOutputTest.java", "JavaccOutputTestConstants.java", "JavaccOutputTestTokenManager.java",
        "ParseException.java", "SimpleCharStream.java", "Token.java", "TokenMgrError.java" };
    
    private CompileJavaccTask task;
    
    @Before
    public void setUp() {
        Project project = ProjectBuilder.builder().build();
        applyJavaccPluginToProject(project);
        
        task = (CompileJavaccTask) project.getTasks().findByName(CompileJavaccTask.TASK_NAME_VALUE);
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
    public void compileJavaccToJavaCompilesEachJavaccInputFileToJava() {
        setTaskInputDirectory("/input");
        final File outputDirectory = setTaskOutputDirectory("output");

        task.execute();

        assertTrue(outputDirectory.isDirectory());
        assertEquals(GENERATED_FILES.length, outputDirectory.list().length);
        assertTrue(Arrays.asList(outputDirectory.list()).containsAll(Arrays.asList(GENERATED_FILES)));
    }

    private void setTaskInputDirectory(final String inputDirectoryName) {
        final File inputDirectory = new File(getClass().getResource(inputDirectoryName).getFile());
        task.setInputDirectory(inputDirectory);
    }
    
    private File setTaskOutputDirectory(final String outputDirectoryName) {
        final File outputDirectory = new File(getClass().getResource("/").getFile() + outputDirectoryName);
        task.setOutputDirectory(outputDirectory);
        return outputDirectory;
    }
    
    @Test
    public void compileJavaccToJavaDoesNotGenerateAnythingIfNoInputFiles() {
        testExecuteTaskWithNoInputFiles(new File[0]);
    }
    
    private void testExecuteTaskWithNoInputFiles(File[] noInputFiles) {
        final File inputDirectory = mock(File.class, Answers.RETURNS_MOCKS.get());
        when(inputDirectory.listFiles()).thenReturn(noInputFiles);
        when(inputDirectory.exists()).thenReturn(Boolean.TRUE);
        when(inputDirectory.isDirectory()).thenReturn(Boolean.TRUE);
        task.setSource(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);

        task.execute();

        assertFalse(outputDirectory.isDirectory());
    }
    
    @Test
    public void compileJavaccToJavaDoesNotGenerateAnythingIfInputFilesNull() {
        testExecuteTaskWithNoInputFiles(null);
    }
    
    @Test
    public void compileJavaccToJavaCompilesEachJavaccInputFileToJavaIntoItsPackage() {
        setTaskInputDirectory("/inputWithPackages");
        final File outputDirectory = setTaskOutputDirectory("output");

        task.execute();

        assertTrue(outputDirectory.isDirectory());
        assertEquals(1, outputDirectory.list().length);
        assertEquals("test", outputDirectory.list()[0]);
        final String[] filesInTestPackageUnderOutputDirectory = outputDirectory.listFiles()[0].list();
        assertEquals(GENERATED_FILES.length, filesInTestPackageUnderOutputDirectory.length);
        assertTrue(Arrays.asList(filesInTestPackageUnderOutputDirectory).containsAll(Arrays.asList(GENERATED_FILES)));
    }
    
    @Test(expected = TaskExecutionException.class)
    public void compileJavaccFailsWhenParserGeneratesAnError() {
        setTaskInputDirectory("/inputWithErrors");
        setTaskOutputDirectory("output");

        task.execute();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void inputDirectoryIsMandatory() {
        task.setSource(null);
        setTaskOutputDirectory("output");
        
        task.execute();
    }
    
    @Test(expected = TaskValidationException.class)
    public void outputDirectoryIsMandatory() {
        setTaskInputDirectory("/input");
        task.setOutputDirectory(null);
        
        try {
            task.execute();
        } catch (TaskExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
            throw e;
        }
    }
    
    @Test
    public void taskInputsAreInputDirectory() {
        setTaskInputDirectory("/input");
        
        task.execute();
        
        FileCollection inputFiles = task.getInputs().getFiles();
        assertEquals(1, inputFiles.getFiles().size());
        assertEquals("JavaccOutputTest.jj", ((File) inputFiles.getFiles().toArray()[0]).getName());
    }
    
    @Test
    public void taskOutputsAreOutputDirectory() {
        setTaskInputDirectory("/input");
        setTaskOutputDirectory("output");
        
        task.execute();
        
        FileCollection outputFiles = task.getOutputs().getFiles();
        assertEquals(1, outputFiles.getFiles().size());
        assertEquals("output", ((File) outputFiles.getFiles().toArray()[0]).getName());
    }
    
    @Test
    public void javaccArgumentsAreOutputDirectoryAndFileToCompileWhenNoJavaccArgumentsProvided() {
        setTaskInputDirectory("/input");
        File outputDirectory = setTaskOutputDirectory("output");
        File javaccFile = mock(File.class);
        final String inputFileAbsolutePath = task.getSource().getAsPath();
        when(javaccFile.getAbsolutePath()).thenReturn(inputFileAbsolutePath);
        when(javaccFile.getParentFile()).thenReturn(task.getSource().getSingleFile().getParentFile());
        
        String[] javaccArgumentsForCommandLine = task.getJavaccArgumentsForCommandLine(javaccFile);
        
        assertEquals(2, javaccArgumentsForCommandLine.length);
        assertThat(javaccArgumentsForCommandLine, IsArrayContainingInOrder.arrayContaining("-OUTPUT_DIRECTORY=" + outputDirectory.getAbsolutePath(), inputFileAbsolutePath));
    }
    
    @Test
    public void javaccArgumentsAreOutputDirectoryAndFileToCompileWhenEmptyJavaccArgumentsProvided() {
        setTaskInputDirectory("/input");
        File outputDirectory = setTaskOutputDirectory("output");
        File javaccFile = mock(File.class);
        final String inputFileAbsolutePath = task.getSource().getAsPath();
        when(javaccFile.getAbsolutePath()).thenReturn(inputFileAbsolutePath);
        when(javaccFile.getParentFile()).thenReturn(task.getSource().getSingleFile().getParentFile());
        task.setJavaccArguments(new HashMap<String, String>(0));
        
        String[] javaccArgumentsForCommandLine = task.getJavaccArgumentsForCommandLine(javaccFile);
        
        assertEquals(2, javaccArgumentsForCommandLine.length);
        assertThat(javaccArgumentsForCommandLine, IsArrayContainingInOrder.arrayContaining("-OUTPUT_DIRECTORY=" + outputDirectory.getAbsolutePath(), inputFileAbsolutePath));
    }
    
    @Test
    public void javaccArgumentsAreOutputDirectoryFileToCompileAndProvidedArguments() {
        setTaskInputDirectory("/input");
        File outputDirectory = setTaskOutputDirectory("output");
        File javaccFile = mock(File.class);
        final String inputFileAbsolutePath = task.getSource().getAsPath();
        when(javaccFile.getAbsolutePath()).thenReturn(inputFileAbsolutePath);
        when(javaccFile.getParentFile()).thenReturn(task.getSource().getSingleFile().getParentFile());
        LinkedHashMap<String, String> javaccArguments = new LinkedHashMap<String, String>(1);
        javaccArguments.put("static", Boolean.FALSE.toString());
        task.setJavaccArguments(javaccArguments);
        
        String[] javaccArgumentsForCommandLine = task.getJavaccArgumentsForCommandLine(javaccFile);
        
        final int outputDirectoryAndProvidedArgumentAndFileToCompile = 3;
        assertEquals(outputDirectoryAndProvidedArgumentAndFileToCompile, javaccArgumentsForCommandLine.length);
        final Matcher<String[]> containsOuputDirectoryFileToCompileAndOtherProvidedArguments = IsArrayContainingInOrder.arrayContaining(
            "-OUTPUT_DIRECTORY=" + outputDirectory.getAbsolutePath(),
            "-static=false",
            inputFileAbsolutePath);
        assertThat(javaccArgumentsForCommandLine, containsOuputDirectoryFileToCompileAndOtherProvidedArguments);
    }
}
