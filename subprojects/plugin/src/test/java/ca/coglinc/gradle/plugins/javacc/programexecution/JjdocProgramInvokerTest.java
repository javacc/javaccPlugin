package ca.coglinc.gradle.plugins.javacc.programexecution;

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

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.RelativePath;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class JjdocProgramInvokerTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Project project;
    private ProgramInvoker programInvoker;
    private File tempOutputDirectory;

    @Before
    public void setUp() throws Exception {
        project = mock(Project.class);
        Configuration classpath = mock(Configuration.class);
        tempOutputDirectory = testFolder.newFolder("tempOutput");
        programInvoker = new JjdocProgramInvoker(project, classpath, tempOutputDirectory);
    }

    @Test
    public void invokeCompilerExecutesJjdocProgram() throws Exception {
        givenSuccessfulExecution();

        programInvoker.invokeCompiler(new ProgramArguments());

        verify(project).javaexec(isA(JjdocExecutorAction.class));
    }

    private void givenSuccessfulExecution() {
        when(project.javaexec(any(JjdocExecutorAction.class))).thenReturn(new SuccessExecResult());
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
        when(project.javaexec(any(JjdocExecutorAction.class))).thenReturn(execResult);

        return execResult;
    }

    @Test
    public void givenHtmlOutputStyleWhenAugmentArgumentsThenAppendsHtmlOutputFile() throws IOException {
        ProgramArguments arguments = new ProgramArguments();

        givenArgumentsWhenAugmentArgumentsThenExpectOutputFileWithExtension(arguments, "html");
    }

    private void givenArgumentsWhenAugmentArgumentsThenExpectOutputFileWithExtension(ProgramArguments arguments, String extension) throws IOException {
        File inputDirectory = testFolder.newFolder("input");
        RelativePath fileToCompile = new RelativePath(true, "MyClass.jj");

        ProgramArguments augmentedArguments = programInvoker.augmentArguments(inputDirectory, fileToCompile, arguments);

        String expectedOutputFileArgument = String.format("-OUTPUT_FILE=%s" + File.separator + "%s.%s", tempOutputDirectory.getAbsolutePath(), "MyClass", extension);
        assertThat(augmentedArguments.get(augmentedArguments.size() - 1), is(equalTo(expectedOutputFileArgument)));
    }

    @Test
    public void givenTextOutputStyleWhenAugmentArgumentsThenAppendsTextOutputFile() throws IOException {
        ProgramArguments arguments = new ProgramArguments();
        arguments.add("text", Boolean.TRUE.toString());

        givenArgumentsWhenAugmentArgumentsThenExpectOutputFileWithExtension(arguments, "txt");
    }

    @Test
    public void programNameIsJjdoc() {
        String programName = programInvoker.getProgramName();

        assertThat(programName, is(equalTo("JJDoc")));
    }

    @Test
    public void supportsJavaccFiles() {
        String supportedSuffix = programInvoker.supportedSuffix();

        assertThat(supportedSuffix, is(equalTo(".jj")));
    }
}
