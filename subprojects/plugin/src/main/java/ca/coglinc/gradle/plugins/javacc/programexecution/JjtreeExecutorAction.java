package ca.coglinc.gradle.plugins.javacc.programexecution;

import org.gradle.api.Action;
import org.gradle.api.artifacts.Configuration;
import org.gradle.process.JavaExecSpec;

class JjtreeExecutorAction implements Action<JavaExecSpec> {
    private final Configuration classpath;
    private final ProgramArguments arguments;

    public JjtreeExecutorAction(Configuration classpath, ProgramArguments arguments) {
        this.classpath = classpath;
        this.arguments = arguments;
    }

    @Override
    public void execute(JavaExecSpec executor) {
        executor.classpath(classpath);
        executor.setMain("org.javacc.jjtree.Main");
        executor.args((Object[]) arguments.toArray());
        executor.setIgnoreExitValue(true);
    }
}
