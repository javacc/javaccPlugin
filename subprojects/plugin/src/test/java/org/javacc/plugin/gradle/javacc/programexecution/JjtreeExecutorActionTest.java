package org.javacc.plugin.gradle.javacc.programexecution;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.process.JavaExecSpec;
import org.junit.Before;
import org.junit.Test;

public class JjtreeExecutorActionTest {
    private JavaExecSpec javaExecSpec;
    private Configuration providedClasspath;
    private ProgramArguments providedArguments;
    private Action<JavaExecSpec> action;

    @Before
    public void setUp() throws Exception {
        javaExecSpec = JavaExecFactory.createSpec();
        providedClasspath = mock(Configuration.class);
        providedArguments = new ProgramArguments();
        action = new JjtreeExecutorAction(providedClasspath, providedArguments);
    }

    @Test
    public void executorUsesProvidedClasspath() {
        action.execute(javaExecSpec);

        verify(javaExecSpec).classpath(providedClasspath);
    }

    @Test
    public void executorInvokesJjtree() {
        action.execute(javaExecSpec);

        assertEquals("org.javacc.jjtree.Main", javaExecSpec.getMainClass().get());
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
