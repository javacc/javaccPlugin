package ca.coglinc.gradle.plugins.javacc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NonJavaccSourceFileVisitorTest {
    private static final String TEXT_INPUT_FILENAME = "test.txt";
    private static final String TEXT_INPUT_IN_SUBFOLDER_FILENAME = "sub/test.txt";
    private static final String JAVACC_INPUT_FILENAME = "JavaccOutputTest.jj";
    private static final String UNEXISTING_INPUT_FILENAME = "doesnotexist.txt";
    
    private AbstractJavaccTask task;
    private NonJavaccSourceFileVisitor sourceVisitor;
    private File outputDirectory;
    private File inputDirectory;
    
    @Before
    public void createNonJavaccSourceVisitor() {
        task = mock(AbstractJavaccTask.class);
        when(task.supportedSuffix()).thenReturn(".jj");
        
        outputDirectory = new File("output");
        outputDirectory.mkdirs();
        when(task.getOutputDirectory()).thenReturn(outputDirectory);
        
        inputDirectory = new File(getClass().getResource("/javacc/inputWithNonJavaccFiles").getFile());
        when(task.getInputDirectory()).thenReturn(inputDirectory);
        
        Logger logger = mock(Logger.class);
        when(task.getLogger()).thenReturn(logger);
        
        sourceVisitor = new NonJavaccSourceFileVisitor(task);
    }
    
    @After
    public void cleanOutputDirectory() {
        FileUtils.deleteQuietly(outputDirectory);
    }
    
    @Test
    public void givenATextInputFileWhenVisitFileThenFileIsCopiedToTaskOutputDirectory() {
        FileVisitDetails fileDetails = mock(FileVisitDetails.class);
        when(fileDetails.getFile()).thenReturn(new File(inputDirectory, TEXT_INPUT_FILENAME));
        when(fileDetails.getName()).thenReturn(TEXT_INPUT_FILENAME);
        
        sourceVisitor.visitFile(fileDetails);
        
        assertEquals(1, outputDirectory.list().length);
        assertTrue(Arrays.asList(outputDirectory.list()).contains(TEXT_INPUT_FILENAME));
    }
    
    @Test
    public void givenATextInputFileInSubDirectoryWhenVisitFileThenFileIsCopiedToTaskOutputDirectory() {
        inputDirectory = new File(getClass().getResource("/javacc/inputWithNonJavaccFilesInSubDirectory").getFile());
        when(task.getInputDirectory()).thenReturn(inputDirectory);
        
        FileVisitDetails fileDetails = mock(FileVisitDetails.class);
        when(fileDetails.getFile()).thenReturn(new File(inputDirectory, TEXT_INPUT_IN_SUBFOLDER_FILENAME));
        when(fileDetails.getName()).thenReturn(TEXT_INPUT_IN_SUBFOLDER_FILENAME);
        
        sourceVisitor.visitFile(fileDetails);
        
        assertEquals(1, outputDirectory.list().length);
        assertTrue(Arrays.asList(outputDirectory.list()).contains("sub"));
        
        final File outputSubDirectory = new File(outputDirectory, "sub");
        assertEquals(1, outputSubDirectory.list().length);
        assertTrue(Arrays.asList(outputSubDirectory.list()).contains(TEXT_INPUT_FILENAME));
    }
    
    @Test
    public void givenAJavaccInputFileWhenVisitFileThenFileIsNotCopiedToTaskOutputDirectory() {
        FileVisitDetails fileDetails = mock(FileVisitDetails.class);
        when(fileDetails.getFile()).thenReturn(new File(inputDirectory, JAVACC_INPUT_FILENAME));
        when(fileDetails.getName()).thenReturn(JAVACC_INPUT_FILENAME);
        
        sourceVisitor.visitFile(fileDetails);
        
        assertEquals(0, outputDirectory.list().length);
    }
    
    @Test(expected = JavaccTaskException.class)
    public void givenAnUnexistingTextInputFileWhenVisitFileThenExceptionIsThrown() {
        FileVisitDetails fileDetails = mock(FileVisitDetails.class);
        when(fileDetails.getFile()).thenReturn(new File(UNEXISTING_INPUT_FILENAME));
        when(fileDetails.getName()).thenReturn(UNEXISTING_INPUT_FILENAME);
        
        sourceVisitor.visitFile(fileDetails);
    }
}
