package ca.coglinc.gradle.plugins.javacc.programexecution;

import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.process.JavaExecSpec;

class JjdocExecutorAction implements Action<JavaExecSpec> {
    private final Configuration classpath;
    private final ProgramArguments arguments;

    public JjdocExecutorAction(Configuration classpath, ProgramArguments arguments) {
        this.classpath = classpath;
        this.arguments = arguments;
    }

    @Override
    public void execute(JavaExecSpec executor) {
        executor.classpath(classpath);
        executor.setMain("org.javacc.jjdoc.JJDocMain");
        executor.args((Object[]) arguments.toArray());
        executor.setIgnoreExitValue(true);
    }
}
