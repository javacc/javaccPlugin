package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.FileVisitor;
import org.gradle.api.file.RelativePath;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.JavaCompile;
import org.javacc.parser.Main;

import ca.coglinc.gradle.plugins.javacc.compilationresults.CompiledJavaccFile;
import ca.coglinc.gradle.plugins.javacc.compilationresults.CompiledJavaccFilesDirectory;
import ca.coglinc.gradle.plugins.javacc.compilationresults.CompiledJavaccFilesDirectoryFactory;

public class CompileJavaccTask extends AbstractJavaccTask {
    public static final String TASK_NAME_VALUE = "compileJavacc";
    public static final String TASK_DESCRIPTION_VALUE = "Compiles JavaCC files into Java files";

    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "javacc";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "javacc";
    private static final String SUPPORTED_FILE_SUFFIX = ".jj";
    
    private CompiledJavaccFilesDirectoryFactory compiledJavaccFilesDirectoryFactory = new CompiledJavaccFilesDirectoryFactory();

    public CompileJavaccTask() {
        super(CompileJavaccTask.DEFAULT_INPUT_DIRECTORY, CompileJavaccTask.DEFAULT_OUTPUT_DIRECTORY, "**/*" + SUPPORTED_FILE_SUFFIX);
    }

    @TaskAction
    public void run() {
        getTempOutputDirectory().mkdirs();

        compileSourceFilesToTempOutputDirectory();
        copyCompiledFilesFromTempOutputDirectoryToOutputDirectory();
        copyNonJavaccFilesToOutputDirectory();
        FileUtils.deleteQuietly(getTempOutputDirectory());
    }

    private void copyNonJavaccFilesToOutputDirectory() {
        getSource().visit(getNonJavaccSourceFileVisitor());
    }

    private File getTempOutputDirectory() {
        return new File(getOutputDirectory(), "tmp");
    }

    private void compileSourceFilesToTempOutputDirectory() {
        getSource().visit(getJavaccSourceFileVisitor());
    }

    private void copyCompiledFilesFromTempOutputDirectoryToOutputDirectory() {
        CompiledJavaccFilesDirectory compiledJavaccFilesDirectory
            = compiledJavaccFilesDirectoryFactory.getCompiledJavaccFilesDirectory(getTempOutputDirectory(), getSourceTree(), getOutputDirectory(), getLogger());
        
        for (CompiledJavaccFile compiledJavaccFile : compiledJavaccFilesDirectory.listFiles()) {
            if (compiledJavaccFile.customAstClassExists()) {
                compiledJavaccFile.copyCustomAstClassToTargetDirectory();
            } else {
                compiledJavaccFile.copyCompiledFileToTargetDirectory();
            }
        }
    }

    private FileTree getSourceTree() {
        FileTree sourceTree = getSource();
        
        TaskCollection<JavaCompile> javaCompileTasks = this.getProject().getTasks().withType(JavaCompile.class);
        for (JavaCompile task : javaCompileTasks) {
            sourceTree = sourceTree.plus(task.getSource());
        }
        
        Spec<File> outputDirectoryFilter = new Spec<File>() {

            @Override
            public boolean isSatisfiedBy(File file) {
                return file.getAbsolutePath().contains(getOutputDirectory().getAbsolutePath());
            }
        };
        
        sourceTree = sourceTree.minus(sourceTree.filter(outputDirectoryFilter)).getAsFileTree();
        return sourceTree;
    }

    @Override
    protected void augmentArguments(File inputDirectory, RelativePath inputRelativePath, Map<String, String> arguments) {
        arguments.put("OUTPUT_DIRECTORY", inputRelativePath.getFile(getTempOutputDirectory()).getParentFile().getAbsolutePath());
    }

    @Override
    protected String getProgramName() {
        return "JavaCC";
    }

    @Override
    protected void invokeCompiler(String[] arguments) throws Exception {
        int errorCode = Main.mainProgram(arguments);
        if (errorCode != 0) {
            throw new IllegalStateException("Javacc failed with error code: [" + errorCode + "]");
        }
    }

    @Override
    protected FileVisitor getJavaccSourceFileVisitor() {
        return new JavaccSourceFileVisitor(this);
    }

    @Override
    protected String supportedSuffix() {
        return SUPPORTED_FILE_SUFFIX;
    }
}
