package ca.coglinc.gradle.plugins.javacc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.TaskExecutionException;
import org.gradle.api.tasks.TaskValidationException;
import org.gradle.testfixtures.ProjectBuilder;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.javacc.parser.Main;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Main.class)
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
        final File inputDirectory = new File(getClass().getResource("/input").getFile());
        task.setInputDirectory(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);

        task.execute();

        assertTrue(outputDirectory.isDirectory());
        assertEquals(GENERATED_FILES.length, outputDirectory.list().length);
        assertTrue(Arrays.asList(outputDirectory.list()).containsAll(Arrays.asList(GENERATED_FILES)));
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
        task.setInputDirectory(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);

        task.execute();

        assertTrue(outputDirectory.isDirectory());
        assertEquals(0, outputDirectory.list().length);
    }
    
    @Test
    public void compileJavaccToJavaDoesNotGenerateAnythingIfInputFilesNull() {
        testExecuteTaskWithNoInputFiles(null);
    }
    
    @Test
    public void compileJavaccToJavaCompilesEachJavaccInputFileToJavaIntoItsPackage() {
        final File inputDirectory = new File(getClass().getResource("/inputWithPackages").getFile());
        task.setInputDirectory(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);

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
        final File inputDirectory = new File(getClass().getResource("/inputWithErrors").getFile());
        task.setInputDirectory(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);

        task.execute();
    }
    
    @Test(expected = TaskValidationException.class)
    public void inputDirectoryIsMandatory() {
        task.setInputDirectory(null);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);
        
        try {
            task.execute();
        } catch (TaskExecutionException e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
            throw e;
        }
    }
    
    @Test(expected = TaskValidationException.class)
    public void outputDirectoryIsMandatory() {
        final File inputDirectory = new File(getClass().getResource("/input").getFile());
        task.setInputDirectory(inputDirectory);
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
        final File inputDirectory = new File(getClass().getResource("/input").getFile());
        task.setInputDirectory(inputDirectory);
        
        task.execute();
        
        FileCollection inputFiles = task.getInputs().getFiles();
        assertEquals(1, inputFiles.getFiles().size());
        assertEquals("JavaccOutputTest.jj", ((File) inputFiles.getFiles().toArray()[0]).getName());
    }
    
    @Test
    public void taskOutputsAreOutputDirectory() {
        final File inputDirectory = new File(getClass().getResource("/input").getFile());
        task.setInputDirectory(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);
        
        task.execute();
        
        FileCollection outputFiles = task.getOutputs().getFiles();
        assertEquals(1, outputFiles.getFiles().size());
        assertEquals("output", ((File) outputFiles.getFiles().toArray()[0]).getName());
    }
    
    @Test
    public void providedJavaccArgumentsArePassedToJavacc() throws Exception {
        final File inputDirectory = new File(getClass().getResource("/input").getFile());
        task.setInputDirectory(inputDirectory);
        final File outputDirectory = new File(getClass().getResource("/").getFile() + "output");
        task.setOutputDirectory(outputDirectory);
        Map<String, String> javaccArguments = new HashMap<String, String>(1);
        javaccArguments.put("static", Boolean.TRUE.toString());
        task.setJavaccArguments(javaccArguments);

        task.execute();

        PowerMockito.verifyStatic();
        ArrayContainsAllElementsInMapMapMatcher containsJavaccArguments = new ArrayContainsAllElementsInMapMapMatcher(javaccArguments);
        Main.mainProgram(Mockito.argThat(containsJavaccArguments));
        
        assertTrue(outputDirectory.isDirectory());
        assertEquals(GENERATED_FILES.length, outputDirectory.list().length);
        assertTrue(Arrays.asList(outputDirectory.list()).containsAll(Arrays.asList(GENERATED_FILES)));
    }
    
    private static class ArrayContainsAllElementsInMapMapMatcher extends BaseMatcher<String[]> {
        private Map<String, String> expectedElements;

        public ArrayContainsAllElementsInMapMapMatcher(Map<String, String> expectedJavaccArguments) {
            this.expectedElements = expectedJavaccArguments;
        }

        @Override
        public void describeTo(Description description) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public boolean matches(Object item) {
            if (item instanceof String[]) {
                return false;
            }
            
            List<String> providedElements = Arrays.asList((String[]) item);
            return providedElements.containsAll(expectedElements.values());
        }
    }
}
