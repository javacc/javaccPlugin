package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.util.Map;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.javacc.parser.Main;

public class CompileJavaccTask extends DefaultTask {
    public static final String TASK_NAME_VALUE = "compileJavacc";
    public static final String TASK_DESCRIPTION_VALUE = "Compiles javacc files into java files";
    public static final String JAVACC_GROUP = "JavaCC";
    
    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "javacc";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "javacc";
    
    private File inputDirectory = new File(getProject().getRootDir().getAbsolutePath() + DEFAULT_INPUT_DIRECTORY);
    private File outputDirectory = new File(getProject().getBuildDir().getAbsolutePath() + DEFAULT_OUTPUT_DIRECTORY);
    
    @TaskAction
    public void executeTask() throws Exception {
        outputDirectory.mkdirs();
        
        File[] inputFiles = inputDirectory.listFiles();
        compileInputFilesToJava(inputFiles);
    }
    
    private void compileInputFilesToJava(File[] inputFiles) throws Exception {
        if (hasInputFiles(inputFiles)) {
            forEachInputFileCompile(inputFiles);
        }
    }
    
    private boolean hasInputFiles(File[] inputFiles) {
        return (inputFiles != null) && (inputFiles.length > 0);
    }

    private void forEachInputFileCompile(File[] inputFiles) throws Exception {
        for (File javaccFile : inputFiles) {
            compileToJava(javaccFile);
        }
    }

    private void compileToJava(File javaccFile) throws Exception {
        if (javaccFile.isDirectory()) {
            compileInputFilesToJava(javaccFile.listFiles());
        } else {
            int errorCode = Main.mainProgram(new String[] {getJavaccOutputDirectoryOption(javaccFile.getParentFile()), javaccFile.getAbsolutePath()});
            if (errorCode != 0) {
                throw new IllegalStateException("Javacc failed with error code: [" + errorCode + "]");
            }
        }
    }

    private String getJavaccOutputDirectoryOption(File parentFile) {
        return "-OUTPUT_DIRECTORY=" + outputDirectory.getAbsolutePath() + parentFile.getAbsolutePath().replace(inputDirectory.getAbsolutePath(), "");
    }
    
    void setInputDirectory(File inputDirectory) {
        this.inputDirectory = inputDirectory;
    }
    
    void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    
    @InputDirectory
    public File getInputDirectory() {
        return inputDirectory;
    }
    
    @OutputDirectory
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setJavaccArguments(Map<String, String> javaccArguments) {
        // TODO Auto-generated method stub
        
    }
}
