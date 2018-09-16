package ca.coglinc.gradle.plugins.javacc.compiler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.gradle.api.file.FileTree;
import org.gradle.api.file.RelativePath;
import org.gradle.api.logging.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;

import ca.coglinc.gradle.plugins.javacc.JavaccTaskException;
import ca.coglinc.gradle.plugins.javacc.compilationresults.CompiledJavaccFile;
import ca.coglinc.gradle.plugins.javacc.compilationresults.CompiledJavaccFilesDirectory;
import ca.coglinc.gradle.plugins.javacc.compilationresults.CompiledJavaccFilesDirectoryFactory;
import ca.coglinc.gradle.plugins.javacc.programexecution.ProgramArguments;
import ca.coglinc.gradle.plugins.javacc.programexecution.ProgramInvoker;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JavaccSourceFileCompilerTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File inputFolder;
    private File outputFolder;
    private ProgramArguments programArguments;
    private ProgramInvoker programInvoker;
    private FileTree source;
    private CompilerInputOutputConfiguration compilerInputOutputConfiguration;
    private Logger logger;
    private SourceFileCompiler compiler;

    @Before
    public void setUp() throws Exception {
        inputFolder = testFolder.newFolder("input");
        outputFolder = testFolder.newFolder("output");

        programArguments = givenProgramArguments();
        programInvoker = givenProgramInvoker(programArguments);
        source = givenSources();
        compilerInputOutputConfiguration = givenCompilerConfiguration(source);
        logger = givenLogger();

        compiler = new JavaccSourceFileCompiler(programInvoker, programArguments, compilerInputOutputConfiguration, logger);
    }

    private ProgramArguments givenProgramArguments() {
        ProgramArguments programArguments = new ProgramArguments();
        programArguments.add("name", "value");

        return programArguments;
    }

    private ProgramInvoker givenProgramInvoker(ProgramArguments programArguments) {
        ProgramInvoker programInvoker = mock(ProgramInvoker.class);
        when(programInvoker.augmentArguments(any(File.class), any(RelativePath.class), any(ProgramArguments.class))).thenReturn(programArguments);
        return programInvoker;
    }

    private FileTree givenSources() {
        return mock(FileTree.class);
    }

    private CompilerInputOutputConfiguration givenCompilerConfiguration(FileTree source) throws IOException {
        File tempOutputDirectory = spy(testFolder.newFolder());

        CompilerInputOutputConfiguration configuration = mock(CompilerInputOutputConfiguration.class);
        when(configuration.getTempOutputDirectory()).thenReturn(tempOutputDirectory);
        when(configuration.getSource()).thenReturn(source);

        return configuration;
    }

    private Logger givenLogger() {
        return mock(Logger.class);
    }

    @Test
    public void compileSourceFilesToTempOutputDirectoryVisitsJavaccSources() {
        compiler.compileSourceFilesToTempOutputDirectory();

        verify(source).visit(isA(JavaccSourceFileVisitor.class));
    }

    @Test
    public void copyNonJavaccFilesToOutputDirectoryVisitsNonJavaccSources() {
        compiler.copyNonJavaccFilesToOutputDirectory();

        verify(source).visit(isA(NonJavaccSourceFileVisitor.class));
    }

    @Test
    public void compileInvokesProgramCompiler() throws Exception {
        RelativePath fileToCompile = givenFileToCompile();

        compiler.compile(inputFolder, fileToCompile);

        ArgumentCaptor<ProgramArguments> programArgumentsCaptor = ArgumentCaptor.forClass(ProgramArguments.class);
        verify(programInvoker).augmentArguments(same(inputFolder), same(fileToCompile), programArgumentsCaptor.capture());
        assertThat(Arrays.asList(programArguments.toArray()), everyItem(isIn(programArgumentsCaptor.getValue().toArray())));

        verify(programInvoker).invokeCompiler(programArgumentsCaptor.capture());
        assertThat(Arrays.asList(programArguments.toArray()), everyItem(isIn(programArgumentsCaptor.getValue().toArray())));
    }

    private RelativePath givenFileToCompile() {
        return new RelativePath(true, "File.jj");
    }

    @Test
    public void compileThrowsExceptionWhenUnderlyingProgramInvokerFails() throws Exception {
        Exception programFailure = new RuntimeException();
        doThrow(programFailure).when(programInvoker).invokeCompiler(programArguments);

        RelativePath fileToCompile = givenFileToCompile();

        thrown.expect(JavaccTaskException.class);
        thrown.expectCause(is(programFailure));

        compiler.compile(inputFolder, fileToCompile);
    }

    @Test
    public void givenACustomAstClassExistsInJavaSourceTreeWhenCopyCompiledFilesFromTempOutputDirectoryToOutputDirectoryThenCustomAstClassIsUsed() {
        CompiledJavaccFile compiledFile = givenACompiledFileWithACustomAstClassInJavaSourceTree();

        compiler.copyCompiledFilesFromTempOutputDirectoryToOutputDirectory();

        verify(compiledFile).ignoreCompiledFileAndUseCustomAstClassFromJavaSourceTree(any(FileTree.class));
    }

    private CompiledJavaccFile givenACompiledFileWithACustomAstClassInJavaSourceTree() {
        CompiledJavaccFile compiledFile = mock(CompiledJavaccFile.class);
        when(compiledFile.customAstClassExists(any(FileTree.class))).thenReturn(true);

        fileIsCompiledByCompiler(compiledFile);

        return compiledFile;
    }

    private void fileIsCompiledByCompiler(CompiledJavaccFile compiledFile) {
        CompiledJavaccFilesDirectory compiledFilesDirectory = mock(CompiledJavaccFilesDirectory.class);
        when(compiledFilesDirectory.listFiles()).thenReturn(Arrays.asList(compiledFile));

        CompiledJavaccFilesDirectoryFactory factory = mock(CompiledJavaccFilesDirectoryFactory.class);
        when(factory.getCompiledJavaccFilesDirectory(any(File.class), any(FileTree.class), any(File.class), any(Logger.class))).thenReturn(compiledFilesDirectory);

        ((JavaccSourceFileCompiler) compiler).setCompiledJavaccFilesDirectoryFactoryForTest(factory);
    }

    @Test
    public void givenACustomAstClassExistsInCompilerSourceTreeWhenCopyCompiledFilesFromTempOutputDirectoryToOutputDirectoryThenCustomAstClassIsUsed() {
        CompiledJavaccFile compiledFile = givenACompiledFileWithACustomAstClassInCompilerSourceTree();

        compiler.copyCompiledFilesFromTempOutputDirectoryToOutputDirectory();

        verify(compiledFile).copyCustomAstClassToTargetDirectory(any(FileTree.class));
    }

    private CompiledJavaccFile givenACompiledFileWithACustomAstClassInCompilerSourceTree() {
        CompiledJavaccFile compiledFile = mock(CompiledJavaccFile.class);
        when(compiledFile.customAstClassExists(any(FileTree.class))).thenReturn(false);
        when(compiledFile.customAstClassExists()).thenReturn(true);

        fileIsCompiledByCompiler(compiledFile);

        return compiledFile;
    }

    @Test
    public void givenNoCustomAstClassExistsWhenCopyCompiledFilesFromTempOutputDirectoryToOutputDirectoryThenCompiledClassIsUsed() {
        CompiledJavaccFile compiledFile = givenACompiledFileWithNoCustomAstClass();

        compiler.copyCompiledFilesFromTempOutputDirectoryToOutputDirectory();

        verify(compiledFile).copyCompiledFileToTargetDirectory();
    }

    private CompiledJavaccFile givenACompiledFileWithNoCustomAstClass() {
        CompiledJavaccFile compiledFile = mock(CompiledJavaccFile.class);
        when(compiledFile.customAstClassExists(any(FileTree.class))).thenReturn(false);
        when(compiledFile.customAstClassExists()).thenReturn(false);

        fileIsCompiledByCompiler(compiledFile);

        return compiledFile;
    }

    @Test
    public void supportedSuffixDelegatesToProgramInvoker() {
        compiler.supportedSuffix();

        verify(programInvoker).supportedSuffix();
    }

    @Test
    public void getProgramNameDelegatesToProgramInvoker() {
        compiler.getProgramName();

        verify(programInvoker).getProgramName();
    }

    @Test
    public void getOutputDirectoryDelegatesToCompilerConfiguration() {
        compiler.getOutputDirectory();

        verify(compilerInputOutputConfiguration).getOutputDirectory();
    }

    @Test
    public void getInputDirectoryDelegatesToCompilerConfiguration() {
        compiler.getInputDirectory();

        verify(compilerInputOutputConfiguration).getInputDirectory();
    }

    @Test
    public void createTempOutputDirectoryCreatesDirectoryAndAllItsParents() {
        compiler.createTempOutputDirectory();

        File tempOutputDirectory = compilerInputOutputConfiguration.getTempOutputDirectory();
        verify(tempOutputDirectory).mkdirs();
        assertTrue(tempOutputDirectory.isDirectory());
        assertTrue(tempOutputDirectory.exists());
    }

    @Test
    public void cleanTempOutputDirectoryDeletesDirectory() {
        compiler.cleanTempOutputDirectory();

        assertFalse(compilerInputOutputConfiguration.getTempOutputDirectory().exists());
    }
}
