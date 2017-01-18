package ca.coglinc.gradle.plugins.javacc.programexecution;

import org.gradle.process.ExecResult;
import org.gradle.process.internal.ExecException;

class SuccessExecResult implements ExecResult {
    @Override
    public int getExitValue() {
        return 0;
    }

    @Override
    public ExecResult assertNormalExitValue() throws ExecException {
        return this;
    }

    @Override
    public ExecResult rethrowFailure() throws ExecException {
        return this;
    }
}
