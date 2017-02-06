package ca.coglinc.gradle.plugins.javacc;

import java.io.File;

import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;

import ca.coglinc.gradle.plugins.javacc.compiler.CompilerInputOutputConfiguration;
import ca.coglinc.gradle.plugins.javacc.compiler.JavaccCompilerInputOutputConfiguration;
import ca.coglinc.gradle.plugins.javacc.compiler.JavaccSourceFileCompiler;
import ca.coglinc.gradle.plugins.javacc.compiler.SourceFileCompiler;
import ca.coglinc.gradle.plugins.javacc.programexecution.JavaccProgramInvoker;
import ca.coglinc.gradle.plugins.javacc.programexecution.ProgramArguments;

public class CompileJavaccTask extends AbstractJavaccTask {
    public static final String TASK_NAME_VALUE = "compileJavacc";
    public static final String TASK_DESCRIPTION_VALUE = "Compiles JavaCC files into Java files";

    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "javacc";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "javacc";

    public CompileJavaccTask() {
        super(CompileJavaccTask.DEFAULT_INPUT_DIRECTORY, CompileJavaccTask.DEFAULT_OUTPUT_DIRECTORY, "**/*" + JavaccProgramInvoker.SUPPORTED_FILE_SUFFIX);
    }

    @TaskAction
    public void run() {
        TaskCollection<JavaCompile> javaCompileTasks = this.getProject().getTasks().withType(JavaCompile.class);
        CompilerInputOutputConfiguration inputOutputDirectories
            = new JavaccCompilerInputOutputConfiguration(getInputDirectory(), getOutputDirectory(), getSource(), javaCompileTasks);
        JavaccProgramInvoker javaccInvoker = new JavaccProgramInvoker(getProject(), getClasspath(), inputOutputDirectories.getTempOutputDirectory());
        ProgramArguments arguments = new ProgramArguments();
        arguments.addAll(getArguments());
        SourceFileCompiler compiler = new JavaccSourceFileCompiler(language, javaccInvoker, arguments, inputOutputDirectories, getLogger());

        compiler.createTempOutputDirectory();
        compiler.compileSourceFilesToTempOutputDirectory();
        compiler.copyCompiledFilesFromTempOutputDirectoryToOutputDirectory();
        compiler.copyNonJavaccFilesToOutputDirectory();
        compiler.cleanTempOutputDirectory();
    }
}
