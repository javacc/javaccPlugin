package ca.coglinc.gradle.plugins.javacc.compilationresults;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doThrow;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.gradle.api.file.FileTree;
import org.gradle.api.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileUtils.class)
public class CompiledJavaccFileTest {
    private File outputDirectory;
    private FileTree customAstClassesDirectory;
    private File targetDirectory;
    private Logger logger;

    @Before
    public void createCompiledJavaccFile() {
        outputDirectory = new File(getClass().getResource("/compiledJavaccFile/output").getFile());
        targetDirectory = new File(getClass().getResource("/compiledJavaccFile/target").getFile());
        logger = mock(Logger.class);
        
        Set<File> sourceTree = new HashSet<File>();
        sourceTree.add(new File(getClass().getResource("/compiledJavaccFile/customAstClasses").getFile()));
        customAstClassesDirectory = mock(FileTree.class);
        when(customAstClassesDirectory.getFiles()).thenReturn(sourceTree);
    }
    
    @After
    public void deleteFiles() throws IOException {
        File targetDirectory = new File(getClass().getResource("/compiledJavaccFile/target").getFile());
        FileUtils.cleanDirectory(targetDirectory);
    }

    @Test
    public void customAstClassDoesNotExist() {
        File file = new File(outputDirectory, "FileWithNoCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, customAstClassesDirectory, targetDirectory, logger);
        
        boolean customAstClassExists = compiledJavaccFile.customAstClassExists();
        
        assertFalse(customAstClassExists);
    }
    
    @Test
    public void customAstClassExists() {
        File file = new File(outputDirectory, "FileWithCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, customAstClassesDirectory, targetDirectory, logger);
        
        boolean customAstClassExists = compiledJavaccFile.customAstClassExists();
        
        assertTrue(customAstClassExists);
    }
    
    @Test
    public void copyCompiledFileToTargetDirectory() {
        File expectedFile = new File(targetDirectory, "FileWithNoCorrespondingCustomAstClass.java");
        assertFalse(expectedFile.exists());
        
        File file = new File(outputDirectory, "FileWithNoCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, customAstClassesDirectory, targetDirectory, logger);
        
        compiledJavaccFile.copyCompiledFileToTargetDirectory();
        
        assertTrue(expectedFile.exists());
    }
    
    @Test(expected = CompiledJavaccFileOperationException.class)
    public void copyCompiledFileToTargetDirectoryFails() {
        File file = new File(outputDirectory, "DoesNotExist.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, customAstClassesDirectory, targetDirectory, logger);
        
        compiledJavaccFile.copyCompiledFileToTargetDirectory();
    }
    
    @Test
    public void copyCustomAstClassToTargetDirectory() {
        File expectedFile = new File(targetDirectory, "FileWithCorrespondingCustomAstClass.java");
        assertFalse(expectedFile.exists());
        
        File file = new File(outputDirectory, "FileWithCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, customAstClassesDirectory, targetDirectory, logger);
        
        compiledJavaccFile.copyCustomAstClassToTargetDirectory();
        
        assertTrue(expectedFile.exists());
    }
    
    @Test(expected = CompiledJavaccFileOperationException.class)
    public void copyCustomAstClassToTargetDirectoryFails() throws Exception {
        PowerMockito.mockStatic(FileUtils.class, Answers.CALLS_REAL_METHODS.get());
        doThrow(new IOException()).when(FileUtils.class, "copyFile", any(File.class), any(File.class));
        
        File file = new File(outputDirectory, "FileWithCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, customAstClassesDirectory, targetDirectory, logger);
        
        compiledJavaccFile.copyCustomAstClassToTargetDirectory();
    }
}
