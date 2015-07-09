package ca.coglinc.gradle.plugins.javacc;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.RelativePath;
import org.gradle.api.logging.Logger;
import org.junit.Before;
import org.junit.Test;

public class JavaccSourceFileVisitorTest {
    private static final String JAVACC_SOURCE_FILE = "Parser.jj";
    private static final String JAVA_SOURCE_FILE = "TokenMgr.java";
    
    private AbstractJavaccTask task;
    private JavaccSourceFileVisitor sourceVisitor;
    
    @Before
    public void createJavaccSourceVisitor() {
        task = mock(AbstractJavaccTask.class);
        when(task.supportedSuffix()).thenReturn(".jj");
        
        Logger logger = mock(Logger.class);
        when(task.getLogger()).thenReturn(logger);
        
        sourceVisitor = new JavaccSourceFileVisitor(task);
    }

    @Test
    public void givenAJavaccSourceFileWhenVisitFileThenDelegatesToTaskCompile() {
        FileVisitDetails fileDetails = mock(FileVisitDetails.class);
        when(fileDetails.getFile()).thenReturn(new File(JAVACC_SOURCE_FILE));
        when(fileDetails.getName()).thenReturn(JAVACC_SOURCE_FILE);
        when(fileDetails.getPath()).thenReturn(JAVACC_SOURCE_FILE);
        
        sourceVisitor.visitFile(fileDetails);
        
        verify(task).compile(any(File.class), any(RelativePath.class));
    }
    
    @Test
    public void givenAJavaSourceFileWhenVisitFileThenDoesNotDelegateToTaskCompile() {
        FileVisitDetails fileDetails = mock(FileVisitDetails.class);
        when(fileDetails.getFile()).thenReturn(new File(JAVA_SOURCE_FILE));
        when(fileDetails.getName()).thenReturn(JAVA_SOURCE_FILE);
        when(fileDetails.getPath()).thenReturn(JAVA_SOURCE_FILE);
        
        sourceVisitor.visitFile(fileDetails);
        
        verify(task, never()).compile(any(File.class), any(RelativePath.class));
    }
}
