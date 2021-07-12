package org.javacc.plugin.gradle.javacc.programexecution;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.process.ExecResult;
import org.gradle.process.JavaExecSpec;

abstract class AbstractProgramInvoker implements ProgramInvoker {
    protected final File tempOutputDirectory;

    private final Project project;
    private final Configuration classpath;
    private final Class<? extends Action<JavaExecSpec>> executorType;

    protected AbstractProgramInvoker(Project project, Configuration classpath, File tempOutputDirectory, Class<? extends Action<JavaExecSpec>> executorType) {
        this.project = project;
        this.classpath = classpath;
        this.tempOutputDirectory = tempOutputDirectory;
        this.executorType = executorType;
    }

    @Override
    public void invokeCompiler(ProgramArguments arguments) throws Exception {
        ExecResult execResult = project.javaexec(executor(arguments));

        if (execResult.getExitValue() != 0) {
            throw new IllegalStateException("JJTree failed with error code: [" + execResult.getExitValue() + "]");
        }
    }

    private Action<JavaExecSpec> executor(ProgramArguments arguments) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<? extends Action<JavaExecSpec>> constructor = executorType.getConstructor(Configuration.class, ProgramArguments.class);

        return constructor.newInstance(classpath, arguments);
    }
}
