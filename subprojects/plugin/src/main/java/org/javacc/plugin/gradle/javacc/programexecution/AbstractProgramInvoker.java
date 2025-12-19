package org.javacc.plugin.gradle.javacc.programexecution;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.gradle.api.Action;
import org.gradle.api.file.FileCollection;
import org.gradle.process.ExecOperations;
import org.gradle.process.ExecResult;
import org.gradle.process.JavaExecSpec;

abstract class AbstractProgramInvoker implements ProgramInvoker {

    protected static final int VERSION_4 = 4;
    protected static final int VERSION_8 = 8;
    protected final File tempOutputDirectory;

    private final FileCollection classpath;
    private final String javaccVersion;
    private final Class<? extends Action<JavaExecSpec>> executorType;
    private final ExecOperations execOperations;

    protected AbstractProgramInvoker(FileCollection classpath, String javaccVersion,
            File tempOutputDirectory, Class<? extends Action<JavaExecSpec>> executorType,
            ExecOperations ops) {
        this.classpath = classpath;
        this.javaccVersion = javaccVersion;
        this.tempOutputDirectory = tempOutputDirectory;
        this.executorType = executorType;
        this.execOperations = ops;
    }

    @Override
    public void invokeCompiler(ProgramArguments arguments) throws Exception {
        ExecResult execResult = execOperations.javaexec(executor(arguments));

        if (execResult.getExitValue() != 0) {
            throw new IllegalStateException("JJTree failed with error code: [" + execResult.getExitValue() + "]");
        }
    }

    private Action<JavaExecSpec> executor(ProgramArguments arguments) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<? extends Action<JavaExecSpec>> constructor = executorType.getConstructor(FileCollection.class, ProgramArguments.class);

        return constructor.newInstance(classpath, arguments);
    }

    protected void addCodeGenerator(ProgramArguments augmentedArguments) {
        Integer[] version = getJavaccVersion();
        if (version.length >  0 && version[0] >= VERSION_8) {
            augmentedArguments.add("CODE_GENERATOR", "java");
        }
    }

    protected Integer[] getJavaccVersion() {
        return Arrays.stream(javaccVersion.split("\\.")).map(this::safeParseInt).toArray(Integer[]::new);
    }

    private int safeParseInt(String num) {
        try {
            return Integer.parseInt(num);
        } catch (RuntimeException ignore) {
            // non-numeric version
        }
        return 0;
    }
}
