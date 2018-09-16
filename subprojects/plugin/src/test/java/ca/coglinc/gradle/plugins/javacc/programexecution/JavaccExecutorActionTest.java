package ca.coglinc.gradle.plugins.javacc.programexecution;

import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.process.JavaExecSpec;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class JavaccExecutorActionTest {
    private JavaExecSpec javaExecSpec;
    private Configuration providedClasspath;
    private ProgramArguments providedArguments;
    private Action<JavaExecSpec> action;

    @Before
    public void setUp() throws Exception {
        javaExecSpec = mock(JavaExecSpec.class);
        providedClasspath = mock(Configuration.class);
        providedArguments = new ProgramArguments();
        action = new JavaccExecutorAction(providedClasspath, providedArguments);
    }

    @Test
    public void executorUsesProvidedClasspath() {
        action.execute(javaExecSpec);

        verify(javaExecSpec).classpath(providedClasspath);
    }

    @Test
    public void executorInvokesJavacc() {
        action.execute(javaExecSpec);

        verify(javaExecSpec).setMain("org.javacc.parser.Main");
    }

    @Test
    public void executorUsesProvidedArguments() {
        action.execute(javaExecSpec);

        verify(javaExecSpec).args((Object[]) providedArguments.toArray());
    }

    @Test
    public void executorDoesNotInterruptExecutionInCaseOfError() {
        action.execute(javaExecSpec);

        verify(javaExecSpec).setIgnoreExitValue(true);
    }
}
