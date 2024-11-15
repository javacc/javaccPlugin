package org.javacc.plugin.gradle.javacc;

import java.io.File;

import javax.inject.Inject;

import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

import org.javacc.plugin.gradle.javacc.compiler.CompilerInputOutputConfiguration;
import org.javacc.plugin.gradle.javacc.compiler.JavaccCompilerInputOutputConfiguration;
import org.javacc.plugin.gradle.javacc.compiler.JavaccSourceFileCompiler;
import org.javacc.plugin.gradle.javacc.compiler.SourceFileCompiler;
import org.javacc.plugin.gradle.javacc.programexecution.JjdocProgramInvoker;
import org.javacc.plugin.gradle.javacc.programexecution.ProgramArguments;

@CacheableTask
public class CompileJjdocTask extends AbstractJavaccTask {
    public static final String TASK_NAME_VALUE = "jjdoc";
    public static final String TASK_DESCRIPTION_VALUE = "Takes a JavaCC parser specification and produces documentation for the BNF grammar";

    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "javacc";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "jjdoc";

    @Inject
    public CompileJjdocTask(ExecOperations execOperations) {
        super(DEFAULT_INPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY, "**/*" + JjdocProgramInvoker.SUPPORTED_FILE_SUFFIX, execOperations);
    }

    @TaskAction
    public void run() {
        CompilerInputOutputConfiguration inputOutputDirectories
            = new JavaccCompilerInputOutputConfiguration(getInputDirectory(), getOutputDirectory(), getSource(), javaCompileTasks);
        JjdocProgramInvoker jjdocInvoker = new JjdocProgramInvoker(getClasspath(),
            inputOutputDirectories.getTempOutputDirectory(), execOperations);
        ProgramArguments arguments = new ProgramArguments();
        arguments.addAll(getArguments());
        SourceFileCompiler compiler = new JavaccSourceFileCompiler(jjdocInvoker, arguments, inputOutputDirectories, getLogger());

        compiler.createTempOutputDirectory();
        compiler.compileSourceFilesToTempOutputDirectory();
        compiler.copyCompiledFilesFromTempOutputDirectoryToOutputDirectory();
        compiler.cleanTempOutputDirectory();
    }
}
