package org.javacc.plugin.gradle.javacc.programexecution;

import org.gradle.api.Action;
import org.gradle.api.file.FileCollection;
import org.gradle.process.JavaExecSpec;

class JavaccExecutorAction implements Action<JavaExecSpec> {
    private final FileCollection classpath;
    private final ProgramArguments arguments;

    // CHECKSTYLE:OFF RedundantModifierCheck
    public JavaccExecutorAction(FileCollection classpath, ProgramArguments arguments) {
        this.classpath = classpath;
        this.arguments = arguments;
    }
    // CHECKSTYLE:ON RedundantModifierCheck

    @Override
    public void execute(JavaExecSpec executor) {
        executor.classpath(classpath);
        executor.getMainClass().set("org.javacc.parser.Main");
        executor.args((Object[]) arguments.toArray());
        executor.setIgnoreExitValue(true);
    }
}
