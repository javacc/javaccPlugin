package org.javacc.plugin.gradle.javacc.compilationresults;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.gradle.api.file.FileTree;
import org.junit.Before;
import org.junit.Test;

public class CompiledJavaccFilesDirectoryFactoryTest {
    private CompiledJavaccFilesDirectoryFactory factory;
    private File outputDirectory;
    private FileTree customAstClassesDirectory;
    private File targetDirectory;
    
    @Before
    public void createFactory() {
        factory = new CompiledJavaccFilesDirectoryFactory();
        outputDirectory = new File(getClass().getResource("/empty").getFile());
        targetDirectory = new File(getClass().getResource("/empty").getFile());
        
        Set<File> sourceTree = new HashSet<File>();
        sourceTree.add(new File(getClass().getResource("/empty").getFile()));
        customAstClassesDirectory = mock(FileTree.class);
        when(customAstClassesDirectory.getFiles()).thenReturn(sourceTree);
    }

    @Test
    public void createInstance() {
        CompiledJavaccFilesDirectory directory = factory.getCompiledJavaccFilesDirectory(outputDirectory, customAstClassesDirectory, targetDirectory, null);
        
        assertNotNull(directory);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void outputDirectoryMustBeProvided() {
        factory.getCompiledJavaccFilesDirectory(null, customAstClassesDirectory, targetDirectory, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void outputDirectoryMustExist() {
        File inexistingOutputDirectory = new File(getClass().getResource("/").getFile() + "doesNotExist");
        
        factory.getCompiledJavaccFilesDirectory(inexistingOutputDirectory, customAstClassesDirectory, targetDirectory, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void outputDirectoryMustBeADirectory() {
        File invalidOutputDirectory = new File(getClass().getResource("/javacc/input/JavaccOutputTest.jj").getFile());
        
        factory.getCompiledJavaccFilesDirectory(invalidOutputDirectory, customAstClassesDirectory, targetDirectory, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void customAstClassesDirectoryMustBeProvided() {
        factory.getCompiledJavaccFilesDirectory(outputDirectory, null, targetDirectory, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void targetDirectoryMustBeProvided() {
        factory.getCompiledJavaccFilesDirectory(outputDirectory, customAstClassesDirectory, null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void targetDirectoryMustExist() {
        File inexistingOutputDirectory = new File(getClass().getResource("/").getFile() + "doesNotExist");
        
        factory.getCompiledJavaccFilesDirectory(outputDirectory, customAstClassesDirectory, inexistingOutputDirectory, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void targetDirectoryMustBeADirectory() {
        File invalidOutputDirectory = new File(getClass().getResource("/javacc/input/JavaccOutputTest.jj").getFile());
        
        factory.getCompiledJavaccFilesDirectory(outputDirectory, customAstClassesDirectory, invalidOutputDirectory, null);
    }
}
