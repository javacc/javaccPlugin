package org.javacc.plugin.gradle.javacc;

import java.io.File;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;

import org.javacc.plugin.gradle.javacc.compiler.CompilerInputOutputConfiguration;
import org.javacc.plugin.gradle.javacc.compiler.JavaccCompilerInputOutputConfiguration;
import org.javacc.plugin.gradle.javacc.compiler.JavaccSourceFileCompiler;
import org.javacc.plugin.gradle.javacc.compiler.SourceFileCompiler;
import org.javacc.plugin.gradle.javacc.programexecution.JjtreeProgramInvoker;
import org.javacc.plugin.gradle.javacc.programexecution.ProgramArguments;

@CacheableTask
public class CompileJjtreeTask extends AbstractJavaccTask {
    public static final String TASK_NAME_VALUE = "compileJjtree";
    public static final String TASK_DESCRIPTION_VALUE = "Compiles JJTree files into JavaCC files";

    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "jjtree";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "jjtree";

    public CompileJjtreeTask() {
        super(CompileJjtreeTask.DEFAULT_INPUT_DIRECTORY, CompileJjtreeTask.DEFAULT_OUTPUT_DIRECTORY, "**/*" + JjtreeProgramInvoker.SUPPORTED_FILE_SUFFIX);
    }

    @TaskAction
    public void run() {
        TaskCollection<JavaCompile> javaCompileTasks = this.getProject().getTasks().withType(JavaCompile.class);
        CompilerInputOutputConfiguration inputOutputDirectories
            = new JavaccCompilerInputOutputConfiguration(getInputDirectory(), getOutputDirectory(), getSource(), javaCompileTasks);
        JjtreeProgramInvoker jjtreeInvoker = new JjtreeProgramInvoker(getProject(), getClasspath(), inputOutputDirectories.getTempOutputDirectory());
        ProgramArguments arguments = new ProgramArguments();
        arguments.addAll(getArguments());
        SourceFileCompiler compiler = new JavaccSourceFileCompiler(jjtreeInvoker, arguments, inputOutputDirectories, getLogger());

        compiler.createTempOutputDirectory();
        compiler.compileSourceFilesToTempOutputDirectory();
        compiler.copyCompiledFilesFromTempOutputDirectoryToOutputDirectory();
        compiler.copyNonJavaccFilesToOutputDirectory();
        compiler.cleanTempOutputDirectory();
    }
}
