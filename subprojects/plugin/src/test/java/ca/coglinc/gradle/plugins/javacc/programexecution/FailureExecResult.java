package org.javacc.plugin.gradle.javacc.programexecution;

import org.gradle.process.ExecResult;
import org.gradle.process.internal.ExecException;

class FailureExecResult implements ExecResult {
    @Override
    public int getExitValue() {
        return -1;
    }

    @Override
    public ExecResult assertNormalExitValue() throws ExecException {
        throw execException();
    }

    private ExecException execException() {
        return new ExecException(String.format("Non-zero exit value: %d", getExitValue()));
    }

    @Override
    public ExecResult rethrowFailure() throws ExecException {
        throw execException();
    }
}
