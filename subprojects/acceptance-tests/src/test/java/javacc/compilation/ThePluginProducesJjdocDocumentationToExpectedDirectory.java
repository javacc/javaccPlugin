package javacc.compilation;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class ThePluginProducesJjdocDocumentationToExpectedDirectory {

    @Test
    public void givenASimpleProjectWhenExecuteJjdocTaskThenTheDocumentationIsGeneratedInTheDefaultDirectory() throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTest");
        steps.whenIExecuteTask("jjdoc");

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "javacc");
        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjdoc");
        steps.andAssertFileWasGenerated("MyParser.html");
    }
    
    @Test
    public void givenASimpleProjectAndArgumentsAreProvidedWhenExecuteJjdocTaskThenTheDocumentationIsGeneratedInTheDefaultDirectory()
        throws URISyntaxException, IOException {
        
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTestWithArguments");
        steps.whenIExecuteTask("jjdoc");

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "javacc");
        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjdoc");
        steps.andAssertFileWasGenerated("MyParser.txt");
    }
    
    @Test
    public void givenAMultiProjectBuildWhenExecuteCompileJjdocTaskThenTheFilesAreGeneratedInTheDefaultDirectory() throws URISyntaxException,
        IOException {
        
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("multiprojectBuild");
        steps.whenIExecuteTask(":subprojects/subproject1:jjdoc");

        String buildDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "javacc");
        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjdoc");
        steps.andAssertFileWasGenerated("MyParser.html");
        steps.andAssertFileWasGenerated("JavaccOutputTest.html");
    }
    
    @Test
    public void givenASimpleProjectThatConfiguresTheInputOutputDirectoriesWhenExecuteCompileJjdocTaskThenTheFilesAreGeneratedInTheConfiguredDirectory()
        throws URISyntaxException, IOException {
        
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTestWithConfiguredInputsOutputs");
        steps.whenIExecuteTask("jjdoc");

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
        steps.whenIExecuteTask(":subprojects/subproject1:jjdoc");

        String buildDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "generated" + File.separator + "javacc");
        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "generated" + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "outputjjdoc");
        steps.andAssertFileWasGenerated("MyParser.html");
        steps.andAssertFileWasGenerated("JavaccOutputTest.html");
    }
}
