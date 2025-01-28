package org.javacc.plugin.gradle.javacc.programexecution;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.RelativePath;
import org.gradle.process.ExecOperations;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class JavaccProgramInvokerTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    ExecOperations execOperations;
    private ProgramInvoker programInvoker;
    private File tempOutputDirectory;

    @Before
    public void setUp() throws Exception {
        Configuration classpath = mock(Configuration.class);
        tempOutputDirectory = testFolder.newFolder("tempOutput");
        execOperations = mock(ExecOperations.class);
        programInvoker = new JavaccProgramInvoker(classpath, tempOutputDirectory, execOperations);
    }

    @Test
    public void invokeCompilerExecutesJavaccProgram() throws Exception {
        givenSuccessfulExecution();

        programInvoker.invokeCompiler(new ProgramArguments());

        verify(execOperations).javaexec(isA(JavaccExecutorAction.class));
    }

    private void givenSuccessfulExecution() {
        when(execOperations.javaexec(any(JavaccExecutorAction.class))).thenReturn(new SuccessExecResult());
    }

    @Test
    public void invokeCompilerThrowsOnProgramFailure() throws Exception {
        FailureExecResult execResult = givenFailedExecution();

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage(contains(Integer.toString(execResult.getExitValue())));

        programInvoker.invokeCompiler(new ProgramArguments());
    }

    private FailureExecResult givenFailedExecution() {
        FailureExecResult execResult = new FailureExecResult();
        when(execOperations.javaexec(any(JavaccExecutorAction.class))).thenReturn(execResult);

        return execResult;
    }

    @Test
    public void augmentArgumentsAppendsOutputDirectory() throws IOException {
        File inputDirectory = testFolder.newFolder("input");
        RelativePath fileToCompile = new RelativePath(true, "MyClass.jj");

        ProgramArguments augmentedArguments = programInvoker.augmentArguments(inputDirectory, fileToCompile, new ProgramArguments());

        String expectedOutputDirectoryArgument = String.format("-OUTPUT_DIRECTORY=%s", tempOutputDirectory.getAbsolutePath());
        assertThat(augmentedArguments.get(augmentedArguments.size() - 1), is(equalTo(expectedOutputDirectoryArgument)));
    }

    @Test
    public void programNameIsJavacc() {
        String programName = programInvoker.getProgramName();

        assertThat(programName, is(equalTo("JavaCC")));
    }

    @Test
    public void supportsJavaccFiles() {
        String supportedSuffix = programInvoker.supportedSuffix();

        assertThat(supportedSuffix, is(equalTo(".jj")));
    }
}
