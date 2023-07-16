package org.javacc.plugin.gradle.javacc.programexecution;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.process.ExecResult;
import org.gradle.process.JavaExecSpec;

abstract class AbstractProgramInvoker implements ProgramInvoker {

    protected static final int VERSION_4 = 4;
    protected static final int VERSION_8 = 8;
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

    protected void addCodeGenerator(ProgramArguments augmentedArguments) {
        Integer[] version = getJavaccVersion();
        if (version.length >  0 && version[0] >= VERSION_8) {
            augmentedArguments.add("CODE_GENERATOR", "java");
        }
    }

    protected Integer[] getJavaccVersion() {
        String version = "";
        try {
            Configuration configuration = project.getConfigurations().getByName("javacc");
            for (Dependency dependency : configuration.getAllDependencies()) {
                String id = dependency.getGroup() + ":" + dependency.getName();
                if (dependency.getVersion() != null
                    && ("net.java.dev.javacc:javacc".equals(id)
                    || "org.javacc.generator:java".equals(id))) {
                    version = dependency.getVersion();
                }
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        return Arrays.stream(version.split("\\.")).map(this::safeParseInt).toArray(Integer[]::new);
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
