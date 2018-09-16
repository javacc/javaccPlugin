package ca.coglinc.gradle.plugins.javacc.compiler;

import java.io.File;

import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.RelativePath;
import org.gradle.api.logging.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JavaccSourceFileVisitorTest {
    private static final String JAVACC_SOURCE_FILE = "Parser.jj";
    private static final String JAVA_SOURCE_FILE = "TokenMgr.java";

    private Logger logger;
    private SourceFileCompiler compiler;
    private JavaccSourceFileVisitor sourceVisitor;

    @Before
    public void createJavaccSourceVisitor() {
        logger = mock(Logger.class);
        compiler = mock(SourceFileCompiler.class);
        when(compiler.supportedSuffix()).thenReturn(".jj");
        when(compiler.getProgramName()).thenReturn("JavaCC");
        when(compiler.getLogger()).thenReturn(logger);

        sourceVisitor = new JavaccSourceFileVisitor(compiler);
    }

    @Test
    public void givenAJavaccSourceFileWhenVisitFileThenDelegatesToTaskCompile() {
        FileVisitDetails fileDetails = mock(FileVisitDetails.class);
        when(fileDetails.getFile()).thenReturn(new File(JAVACC_SOURCE_FILE));
        when(fileDetails.getName()).thenReturn(JAVACC_SOURCE_FILE);
        when(fileDetails.getPath()).thenReturn(JAVACC_SOURCE_FILE);

        sourceVisitor.visitFile(fileDetails);

        verify(compiler).compile(any(File.class), any(RelativePath.class));
    }

    @Test
    public void givenAJavaSourceFileWhenVisitFileThenDoesNotDelegateToTaskCompile() {
        FileVisitDetails fileDetails = mock(FileVisitDetails.class);
        File fileToVisit = new File(JAVA_SOURCE_FILE);
        when(fileDetails.getFile()).thenReturn(fileToVisit);
        when(fileDetails.getName()).thenReturn(JAVA_SOURCE_FILE);
        when(fileDetails.getPath()).thenReturn(JAVA_SOURCE_FILE);

        sourceVisitor.visitFile(fileDetails);

        verify(compiler, never()).compile(any(File.class), any(RelativePath.class));
        verify(logger).debug(anyString(), eq(fileToVisit.getAbsolutePath()), eq("JavaCC"));
    }
}
