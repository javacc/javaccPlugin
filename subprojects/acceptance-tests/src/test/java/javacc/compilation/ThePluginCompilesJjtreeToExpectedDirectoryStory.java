package javacc.compilation;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class ThePluginCompilesJjtreeToExpectedDirectoryStory {

    @Test
    public void givenAMultiProjectBuildWithJJTreeWhenExecuteCompileJJTreeTaskThenTheFilesAreGeneratedInTheDefaultDirectory()
        throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("multiprojectBuildWithJJTree");
        steps.whenIExecuteTask(":subprojects/subproject1:compileJJTree");

        String buildDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjtree");
        steps.andAssertFileWasGenerated("JJTreeOutputTest.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        steps.andAssertFileWasGenerated("SimpleNode.java");

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "javacc");
    }

    @Test
    public void givenAMultiProjectBuildWithJJTreeThatConfiguresTheInputOutputDirectoriesWhenExecuteCompileJJTreeTaskThenTheFilesAreGeneratedInTheDefaultDirectory()
        throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("multiprojectBuildWithJJTreeAndWithConfiguredInputsOutputs");
        steps.whenIExecuteTask(":subprojects/subproject1:compileJJTree");

        String buildDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build" + File.separator + "output";

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjtree");
        steps.andAssertFileWasGenerated("JJTreeOutputTest.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        steps.andAssertFileWasGenerated("SimpleNode.java");

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "javacc");
    }

    @Test
    public void givenASimpleJJTreeProjectWhenExecuteCompileJJTreeTaskThenTheFilesAreGeneratedInTheDefaultDirectory() throws URISyntaxException,
    IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleJJTreeTest");
        steps.whenIExecuteTask("compileJJTree");

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjtree");
        steps.andAssertFileWasGenerated("JJTreeOutputTest.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        steps.andAssertFileWasGenerated("SimpleNode.java");

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "javacc");
    }

    @Test
    public void givenASimpleJJTreeProjectAndJavaccArgumentsProvidedWhenExecuteCompileJJTreeTaskThenTheFilesAreGeneratedInTheDefaultDirectory()
        throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleJJTreeTestWithArguments");
        steps.whenIExecuteTask("compileJJTree");

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjtree");
        steps.andAssertFileWasGenerated("grammar.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        steps.andAssertFileWasGenerated("SimpleNode.java");

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "javacc");
    }

    @Test
    public void givenASimpleJJTreeProjectThatConfiguresTheInputOutputDirectoriesWhenExecuteCompileJJTreeTaskThenTheFilesAreGeneratedInTheConfiguredDirectory()
        throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleJJTreeTestWithConfiguredInputsOutputs");
        steps.whenIExecuteTask("compileJJTree");

        steps.thenAssertOutputDirectoryExists("build" + File.separator + "output");
        steps.andAssertFileWasGenerated("JJTreeOutputTest.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        steps.andAssertFileWasGenerated("SimpleNode.java");

        steps.thenAssertOutputDirectoryDoesNotExists("build" + File.separator + "generated" + File.separator + "javacc");
    }
}
