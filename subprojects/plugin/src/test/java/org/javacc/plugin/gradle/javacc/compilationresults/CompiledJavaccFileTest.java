package org.javacc.plugin.gradle.javacc.compilationresults;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
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
    private Collection<File> customAstClassesDirectory;
    private File targetDirectory;
    private Logger logger;

    @Before
    public void createCompiledJavaccFile() {
        outputDirectory = new File(getClass().getResource("/compiledJavaccFile/output").getFile());
        targetDirectory = new File(getClass().getResource("/compiledJavaccFile/target").getFile());
        logger = mock(Logger.class);

        Set<File> sourceTree = new HashSet<>();
        sourceTree.add(new File(getClass().getResource("/compiledJavaccFile/customAstClasses").getFile()));
        customAstClassesDirectory = sourceTree;
    }

    @After
    public void deleteFiles() throws IOException {
        File targetDirectory = new File(getClass().getResource("/compiledJavaccFile/target").getFile());
        FileUtils.cleanDirectory(targetDirectory);
    }

    @Test
    public void customAstClassDoesNotExist() {
        File file = new File(outputDirectory, "FileWithNoCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, targetDirectory, logger);

        File customAstFile = compiledJavaccFile.getCustomAstClassInputFile(customAstClassesDirectory);

        assertNull(customAstFile);
    }

    @Test
    public void customAstClassExists() {
        File file = new File(outputDirectory, "FileWithCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, targetDirectory, logger);

        File customAstFile = compiledJavaccFile.getCustomAstClassInputFile(customAstClassesDirectory);

        assertNotNull(customAstFile);
    }

    @Test
    public void customAstClassDoesNotExistInSpecificDirectory() {
        File file = new File(outputDirectory, "FileWithNoCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, targetDirectory, logger);

        File customAstClassExists = compiledJavaccFile.getCustomAstClassInputFile(customAstClassesDirectory);

        assertNull(customAstClassExists);
    }

    @Test
    public void customAstClassExistsInSpecificDirectory() {
        File file = new File(outputDirectory, "FileWithCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, targetDirectory, logger);

        File customAstClassExists = compiledJavaccFile.getCustomAstClassInputFile(customAstClassesDirectory);

        assertNotNull(customAstClassExists);
    }

    @Test
    public void customAstClassCantExistInNullDirectory() {
        File file = new File(outputDirectory, "FileWithCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, targetDirectory, logger);

        File customAstClassExists = compiledJavaccFile.getCustomAstClassInputFile(null);

        assertNull(customAstClassExists);
    }

    @Test
    public void copyCompiledFileToTargetDirectory() {
        File expectedFile = new File(targetDirectory, "FileWithNoCorrespondingCustomAstClass.java");
        assertFalse(expectedFile.exists());

        File file = new File(outputDirectory, "FileWithNoCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, targetDirectory, logger);

        compiledJavaccFile.copyCompiledFileToTargetDirectory();

        assertTrue(expectedFile.exists());
    }

    @Test(expected = CompiledJavaccFileOperationException.class)
    public void copyCompiledFileToTargetDirectoryFails() {
        File file = new File(outputDirectory, "DoesNotExist.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, targetDirectory, logger);

        compiledJavaccFile.copyCompiledFileToTargetDirectory();
    }

    @Test
    public void copyCustomAstClassToTargetDirectory() {
        File expectedFile = new File(targetDirectory, "FileWithCorrespondingCustomAstClass.java");
        assertFalse(expectedFile.exists());

        File file = new File(outputDirectory, "FileWithCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, targetDirectory, logger);

        compiledJavaccFile.handleCustomAstInJavacc(customAstClassesDirectory);

        assertTrue(expectedFile.exists());
    }

    @Test(expected = CompiledJavaccFileOperationException.class)
    public void copyCustomAstClassToTargetDirectoryFails() throws Exception {
        PowerMockito.mockStatic(FileUtils.class, Answers.CALLS_REAL_METHODS.get());
        doThrow(new IOException()).when(FileUtils.class);
        FileUtils.copyFile(any(File.class), any(File.class));

        File file = new File(outputDirectory, "FileWithCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, targetDirectory, logger);

        compiledJavaccFile.handleCustomAstInJavacc(customAstClassesDirectory);
    }

    @Test
    public void toStringReturnsAbsoluteFileName() {
        File file = new File(outputDirectory, "FileWithCorrespondingCustomAstClass.java");
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, targetDirectory, logger);

        String stringValue = compiledJavaccFile.toString();

        assertEquals(file.getAbsolutePath(), stringValue);
    }

    @Test
    public void ignoreCompiledFileAndUseCustomAstClassFromJavaSourceTreeOnlyLogsThatCompiledFileIsNotActedUpon() {
        File file = mock(File.class);
        CompiledJavaccFile compiledJavaccFile = new CompiledJavaccFile(file, outputDirectory, targetDirectory, logger);

        compiledJavaccFile.ignoreCompiledFileAndUseCustomAstClassFromJavaSourceTree(file);

        verify(logger).info(anyString(), eq(file), anyString());
    }
}
