package javacc.compilation;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class ThePluginCompilesJavaccToExpectedDirectoryStory {

    @Test
    public void givenASimpleProjectWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory() throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTest");
        steps.whenIExecuteTask("compileJavacc");
        steps.thenAssertOutputDirectoryExists("build" + File.separator + "generated" + File.separator + "javacc");
        steps.andAssertFileWasGenerated("MyParser.java");
        steps.andAssertFileWasGenerated("MyParserConstants.java");
        steps.andAssertFileWasGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "ParseException.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "SimpleCharStream.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "Token.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "TokenMgrError.java");
    }

    @Test
    public void givenASimpleProjectAndJavaccArgumentsProvidedWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory() throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTestWithArguments");
        steps.whenIExecuteTask("compileJavacc");
        steps.thenAssertOutputDirectoryExists("build" + File.separator + "generated" + File.separator + "javacc");
        steps.andAssertFileWasGenerated("MyParser.java");
        steps.andAssertFileWasGenerated("MyParserConstants.java");
        steps.andAssertFileWasGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "ParseException.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "SimpleCharStream.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "Token.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "TokenMgrError.java");
    }

    @Test
    public void givenAMultiProjectBuildWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory() throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("multiprojectBuild");
        steps.whenIExecuteTask(":subprojects/subproject1:compileJavacc");
        final String outputDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build" + File.separator + "generated" + File.separator + "javacc";
        steps.thenAssertOutputDirectoryExists(outputDirectory);
        steps.andAssertFileWasGenerated("MyParser.java");
        steps.andAssertFileWasGenerated("MyParserConstants.java");
        steps.andAssertFileWasGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "ParseException.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "SimpleCharStream.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "Token.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "TokenMgrError.java");
    }

    @Test
    public void givenASimpleProjectThatConfiguresTheInputOutputDirectoriesWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheConfiguredDirectory() throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTestWithConfiguredInputsOutputs");
        steps.whenIExecuteTask("compileJavacc");
        final String outputDirectory = "build" + File.separator + "output";
        steps.thenAssertOutputDirectoryExists(outputDirectory);
        steps.andAssertFileWasGenerated("MyParser.java");
        steps.andAssertFileWasGenerated("MyParserConstants.java");
        steps.andAssertFileWasGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "ParseException.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "SimpleCharStream.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "Token.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "TokenMgrError.java");
    }

    @Test
    public void givenAMultiProjectBuildThatConfiguresTheInputOutputDirectoriesWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory() throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("multiprojectBuildWithConfiguredInputsOutputs");
        steps.whenIExecuteTask(":subprojects/subproject1:compileJavacc");
        final String outputDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build" + File.separator + "output";
        steps.thenAssertOutputDirectoryExists(outputDirectory);
        steps.andAssertFileWasGenerated("MyParser.java");
        steps.andAssertFileWasGenerated("MyParserConstants.java");
        steps.andAssertFileWasGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "ParseException.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "SimpleCharStream.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "Token.java");
        steps.andAssertFileWasGenerated("test" + File.separator + "pkg" + File.separator + "TokenMgrError.java");
    }

    @Test
    public void givenASimpleJJTreeProjectWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory()
            throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleJJTreeTest");
        steps.whenIExecuteTask("compileJavacc");
        steps.thenAssertOutputDirectoryExists("build" + File.separator + "generated" + File.separator + "jjtree");
        steps.andAssertFileWasGenerated("JJTreeOutputTest.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        steps.andAssertFileWasGenerated("SimpleNode.java");

        steps.thenAssertOutputDirectoryExists("build" + File.separator + "generated" + File.separator + "javacc");
        steps.andAssertFileWasGenerated("Hello.java");
        steps.andAssertFileWasGenerated("HelloTokenManager.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("HelloConstants.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("Token.java");
    }

    @Test
    public void givenASimpleJJTreeProjectAndJavaccArgumentsProvidedWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory()
            throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleJJTreeTestWithArguments");
        steps.whenIExecuteTask("compileJavacc");
        steps.thenAssertOutputDirectoryExists("build" + File.separator + "generated" + File.separator + "jjtree");
        steps.andAssertFileWasGenerated("grammar.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        steps.andAssertFileWasGenerated("SimpleNode.java");

        steps.thenAssertOutputDirectoryExists("build" + File.separator + "generated" + File.separator + "javacc");
        steps.andAssertFileWasGenerated("Hello.java");
        steps.andAssertFileWasGenerated("HelloTokenManager.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("HelloConstants.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("Token.java");
    }

    @Test
    public void givenASimpleJJTreeProjectThatConfiguresTheInputOutputDirectoriesWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheConfiguredDirectory() throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleJJTreeTestWithConfiguredInputsOutputs");
        steps.whenIExecuteTask("compileJavacc");
        final String outputDirectory = "build" + File.separator + "output";
        steps.thenAssertOutputDirectoryExists(outputDirectory);
        steps.andAssertFileWasGenerated("JJTreeOutputTest.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        steps.andAssertFileWasGenerated("SimpleNode.java");

        steps.thenAssertOutputDirectoryExists("build" + File.separator + "generated" + File.separator + "javacc");
        steps.andAssertFileWasGenerated("Hello.java");
        steps.andAssertFileWasGenerated("HelloTokenManager.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("HelloConstants.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("Token.java");
    }
}
