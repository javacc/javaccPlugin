package org.javacc.plugin.gradle.javacc.programexecution;

import org.gradle.api.Action;
import org.gradle.api.file.FileCollection;
import org.gradle.process.JavaExecSpec;

class JjdocExecutorAction implements Action<JavaExecSpec> {
    private final FileCollection classpath;
    private final ProgramArguments arguments;

    // CHECKSTYLE:OFF RedundantModifierCheck
    public JjdocExecutorAction(FileCollection classpath, ProgramArguments arguments) {
        this.classpath = classpath;
        this.arguments = arguments;
    }
    // CHECKSTYLE:ON RedundantModifierCheck

    @Override
    public void execute(JavaExecSpec executor) {
        executor.classpath(classpath);
        executor.getMainClass().set("org.javacc.jjdoc.JJDocMain");
        executor.args((Object[]) arguments.toArray());
        executor.setIgnoreExitValue(true);
    }
}
