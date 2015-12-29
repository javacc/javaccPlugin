package javacc.compilation;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class ThePluginProducesJjdocDocumentationToExpectedDirectory {
    private static final String CLEAN = "clean";
    private static final String JJDOC = "jjdoc";

    @Test
    public void givenASimpleProjectWhenExecuteJjdocTaskThenTheDocumentationIsGeneratedInTheDefaultDirectory() throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTest");
        steps.withArguments(CLEAN, JJDOC).execute();

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "javacc");
        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + JJDOC);
        steps.andAssertFileWasGenerated("MyParser.html");
    }
    
    @Test
    public void givenASimpleProjectWhenRerunJjdocTaskThenTheDocumentationIsGeneratedInTheDefaultDirectory() throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTest");
        steps.withArguments(CLEAN, JJDOC).execute();
        steps.withArguments(JJDOC).withArguments("--rerun-tasks").execute();

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "javacc");
        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + JJDOC);
        steps.andAssertFileWasGenerated("MyParser.html");
    }
    
    @Test
    public void givenASimpleProjectAndArgumentsAreProvidedWhenExecuteJjdocTaskThenTheDocumentationIsGeneratedInTheDefaultDirectory()
        throws URISyntaxException, IOException {
        
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTestWithArguments");
        steps.withArguments(CLEAN, JJDOC).execute();

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "javacc");
        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + JJDOC);
        steps.andAssertFileWasGenerated("MyParser.txt");
    }
    
    @Test
    public void givenAMultiProjectBuildWhenExecuteCompileJjdocTaskThenTheFilesAreGeneratedInTheDefaultDirectory() throws URISyntaxException,
        IOException {
        
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("multiprojectBuild");
        steps.withArguments(CLEAN, ":subprojects/subproject1:jjdoc").execute();

        String buildDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "javacc");
        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + JJDOC);
        steps.andAssertFileWasGenerated("MyParser.html");
        steps.andAssertFileWasGenerated("JavaccOutputTest.html");
    }
    
    @Test
    public void givenASimpleProjectThatConfiguresTheInputOutputDirectoriesWhenExecuteCompileJjdocTaskThenTheFilesAreGeneratedInTheConfiguredDirectory()
        throws URISyntaxException, IOException {
        
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTestWithConfiguredInputsOutputs");
        steps.withArguments(CLEAN, JJDOC).execute();

        String buildDirectory = "build" + File.separator + "outputjjdoc";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "javacc");
        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory);
        steps.andAssertFileWasGenerated("MyParser.html");
        steps.andAssertFileWasGenerated("JavaccOutputTest.html");
    }
    
    @Test
    public void givenAMultiProjectBuildThatConfiguresTheInputOutputDirectoriesWhenExecuteCompileJjdocTaskThenTheFilesAreGeneratedInTheDefaultDirectory()
        throws URISyntaxException, IOException {
        
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("multiprojectBuildWithConfiguredInputsOutputs");
        steps.withArguments(CLEAN, ":subprojects/subproject1:jjdoc").execute();

        String buildDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "generated" + File.separator + "javacc");
        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "generated" + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "outputjjdoc");
        steps.andAssertFileWasGenerated("MyParser.html");
        steps.andAssertFileWasGenerated("JavaccOutputTest.html");
    }
}
