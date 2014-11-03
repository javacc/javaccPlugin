package ca.coglinc.gradle.plugins.javacc;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.javacc.parser.Main;

public class CompileJavaccTask extends SourceTask {
    public static final String TASK_NAME_VALUE = "compileJavacc";
    public static final String TASK_DESCRIPTION_VALUE = "Compiles javacc files into java files";
    public static final String JAVACC_GROUP = "JavaCC";
    
    private static final String DEFAULT_INPUT_DIRECTORY = File.separator + "src" + File.separator + "main" + File.separator + "javacc";
    private static final String DEFAULT_OUTPUT_DIRECTORY = File.separator + "generated" + File.separator + "javacc";
    
    private File inputDirectory = new File(getProject().getProjectDir().getAbsolutePath() + DEFAULT_INPUT_DIRECTORY);
    private File outputDirectory = new File(getProject().getBuildDir().getAbsolutePath() + DEFAULT_OUTPUT_DIRECTORY);
    private Map<String, String> javaccArguments;
    
    public CompileJavaccTask() {
        getLogger().debug("Using [{}] as source folder", inputDirectory);
        setSource(inputDirectory);
        include("**/*.jj");
    }
    
    @TaskAction
    public void executeTask() throws Exception {
        outputDirectory.mkdirs();
        
        Set<File> inputFiles = getSource().getFiles();
        forEachInputFileCompile(inputFiles);
    }
    
    private void forEachInputFileCompile(Set<File> inputFiles) throws Exception {
        for (File javaccFile : inputFiles) {
            compileToJava(javaccFile);
        }
    }

    private void compileToJava(File javaccFile) throws Exception {
        getLogger().debug("Compiling JavaCC file [{}] to [{}]", javaccFile.getAbsolutePath(), outputDirectory.getAbsolutePath());
        
        String[] arguments = getJavaccArgumentsForCommandLine(javaccFile);
        getLogger().debug("Invoking JavaCC with arguments [{}]", (Object[]) arguments);
        
        int errorCode = Main.mainProgram(arguments);
        if (errorCode != 0) {
            throw new IllegalStateException("Javacc failed with error code: [" + errorCode + "]");
        }
    }

    String[] getJavaccArgumentsForCommandLine(File javaccFile) {
        String[] argumentsForCommandLine;
        if (javaccArguments != null && !javaccArguments.isEmpty()) {
            argumentsForCommandLine = new String[javaccArguments.size() + 2];
            
            int index = 1;
            for (Map.Entry<String, String> argumentEntry : javaccArguments.entrySet()) {
                argumentsForCommandLine[index] = String.format("-%1$s=%2$s", argumentEntry.getKey(), argumentEntry.getValue());
                index = index + 1;
            }
        } else {
            argumentsForCommandLine = new String[2];
        }
        
        argumentsForCommandLine[0] = getJavaccOutputDirectoryOption(javaccFile.getParentFile());
        argumentsForCommandLine[argumentsForCommandLine.length - 1] = javaccFile.getAbsolutePath();
        
        return argumentsForCommandLine;
    }

    private String getJavaccOutputDirectoryOption(File parentFile) {
        return "-OUTPUT_DIRECTORY=" + outputDirectory.getAbsolutePath() + parentFile.getAbsolutePath().replace(inputDirectory.getAbsolutePath(), "");
    }
    
    public void setInputDirectory(File inputDirectory) {
        getLogger().debug("Changing source folder to [{}]", inputDirectory);
        this.inputDirectory = inputDirectory;
        setSource(inputDirectory);
        include("**/*.jj");
    }
    
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    
    public File getInputDirectory() {
        return inputDirectory;
    }
    
    @OutputDirectory
    public File getOutputDirectory() {
        return outputDirectory;
    }
    
    public Map<String, String> getJavaccArguments() {
        return javaccArguments;
    }

    public void setJavaccArguments(Map<String, String> javaccArguments) {
        this.javaccArguments = javaccArguments;
    }
}
