package ca.coglinc.gradle.plugins.javacc;

import java.io.File;

import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;

import ca.coglinc.gradle.plugins.javacc.compiler.CompilerInputOutputConfiguration;
import ca.coglinc.gradle.plugins.javacc.compiler.JavaccCompilerInputOutputConfiguration;
import ca.coglinc.gradle.plugins.javacc.compiler.JavaccSourceFileCompiler;
import ca.coglinc.gradle.plugins.javacc.compiler.SourceFileCompiler;
import ca.coglinc.gradle.plugins.javacc.programexecution.JjdocProgramInvoker;
import ca.coglinc.gradle.plugins.javacc.programexecution.ProgramArguments;

public class CompileJjdocTask extends AbstractJavaccTask {
    public static final String TASK_NAME_VALUE = "jjdoc";
    public static final String TASK_DESCRIPTION_VALUE = "Takes a JavaCC parser specification and produces documentation for the BNF grammar";

    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "javacc";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "jjdoc";

    public CompileJjdocTask() {
        super(DEFAULT_INPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY, "**/*" + JjdocProgramInvoker.SUPPORTED_FILE_SUFFIX);
    }

    @TaskAction
    public void run() {
        TaskCollection<JavaCompile> javaCompileTasks = this.getProject().getTasks().withType(JavaCompile.class);
        CompilerInputOutputConfiguration inputOutputDirectories
            = new JavaccCompilerInputOutputConfiguration(getInputDirectory(), getOutputDirectory(), getSource(), javaCompileTasks);
        JjdocProgramInvoker jjdocInvoker = new JjdocProgramInvoker(getProject(), getClasspath(), inputOutputDirectories.getTempOutputDirectory());
        ProgramArguments arguments = new ProgramArguments();
        arguments.addAll(getArguments());
        SourceFileCompiler compiler = new JavaccSourceFileCompiler(jjdocInvoker, arguments, inputOutputDirectories, getLogger());

        compiler.createTempOutputDirectory();
        compiler.compileSourceFilesToTempOutputDirectory();
        compiler.copyCompiledFilesFromTempOutputDirectoryToOutputDirectory();
        compiler.cleanTempOutputDirectory();
    }
}
