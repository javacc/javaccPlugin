package javacc.compilation;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

public class ThePluginCompilesJavaccToExpectedDirectoryStory {
    private static final String CLEAN = "clean";
    private static final String COMPILE_JAVACC = "compileJavacc";

    @Test
    public void givenASimpleProjectWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory() throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTest");
        steps.whenTasks(CLEAN, COMPILE_JAVACC).execute();

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc");
        steps.andAssertFileWasGenerated("MyParser.java");
        steps.andAssertFileWasGenerated("MyParserConstants.java");
        steps.andAssertFileWasGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc" + File.separator + "test" + File.separator + "pkg");
        steps.andAssertFileWasGenerated("JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
    }

    @Test
    public void givenASimpleProjectAndJavaccArgumentsProvidedWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory()
        throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTestWithArguments");
        steps.whenTasks(CLEAN, COMPILE_JAVACC).execute();

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc");
        steps.andAssertFileWasGenerated("MyParser.java");
        steps.andAssertFileWasGenerated("MyParserConstants.java");
        steps.andAssertFileWasGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc" + File.separator + "test" + File.separator + "pkg");
        steps.andAssertFileWasGenerated("JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
    }

    @Test
    public void givenAMultiProjectBuildWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory() throws URISyntaxException,
    IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("multiprojectBuild");
        steps.whenTasks(CLEAN, ":subprojects/subproject1:compileJavacc").execute();

        String buildDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc");
        steps.andAssertFileWasGenerated("MyParser.java");
        steps.andAssertFileWasGenerated("MyParserConstants.java");
        steps.andAssertFileWasGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc" + File.separator + "test" + File.separator + "pkg");
        steps.andAssertFileWasGenerated("JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
    }

    @Test
    public void givenAMultiProjectBuildWithJJTreeWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory()
        throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("multiprojectBuildWithJJTree");
        steps.whenTasks(CLEAN, ":subprojects/subproject1:compileJavacc").execute();

        String buildDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjtree");
        steps.andAssertFileWasGenerated("JJTreeOutputTest.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        steps.andAssertFileWasGenerated("SimpleNode.java");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc");
        steps.andAssertFileWasGenerated("Hello.java");
        steps.andAssertFileWasGenerated("HelloTokenManager.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("HelloConstants.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("Token.java");
    }

    @Test
    public void givenASimpleProjectThatConfiguresTheInputOutputDirectoriesWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheConfiguredDirectory()
        throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTestWithConfiguredInputsOutputs");
        steps.whenTasks(CLEAN, COMPILE_JAVACC).execute();

        String buildDirectory = "build" + File.separator + "output";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory);
        steps.andAssertFileWasGenerated("MyParser.java");
        steps.andAssertFileWasGenerated("MyParserConstants.java");
        steps.andAssertFileWasGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "test" + File.separator + "pkg");
        steps.andAssertFileWasGenerated("JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
    }

    @Test
    public void givenAMultiProjectBuildThatConfiguresTheInputOutputDirectoriesWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory()
        throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("multiprojectBuildWithConfiguredInputsOutputs");
        steps.whenTasks(CLEAN, ":subprojects/subproject1:compileJavacc").execute();

        String buildDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "generated" + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "output");
        steps.andAssertFileWasGenerated("MyParser.java");
        steps.andAssertFileWasGenerated("MyParserConstants.java");
        steps.andAssertFileWasGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "output" + File.separator + "test" + File.separator + "pkg");
        steps.andAssertFileWasGenerated("JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("Token.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
    }

    @Test
    public void givenAMultiProjectBuildWithJJTreeThatConfiguresTheInputOutputDirectoriesWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory()
        throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("multiprojectBuildWithJJTreeAndWithConfiguredInputsOutputs");
        steps.whenTasks(CLEAN, ":subprojects/subproject1:compileJavacc").execute();

        String buildDirectory = "subprojects" + File.separator + "subproject1" + File.separator + "build" + File.separator + "output";

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjtree");
        steps.andAssertFileWasGenerated("JJTreeOutputTest.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        steps.andAssertFileWasGenerated("SimpleNode.java");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc");
        steps.andAssertFileWasGenerated("Hello.java");
        steps.andAssertFileWasGenerated("HelloTokenManager.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("HelloConstants.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("Token.java");
    }

    @Test
    public void givenASimpleJJTreeProjectWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheDefaultDirectory() throws URISyntaxException,
    IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleJJTreeTest");
        steps.whenTasks(CLEAN, COMPILE_JAVACC).execute();

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjtree");
        steps.andAssertFileWasGenerated("JJTreeOutputTest.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        steps.andAssertFileWasGenerated("SimpleNode.java");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc");
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
        steps.whenTasks(CLEAN, COMPILE_JAVACC).execute();

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "jjtree");
        steps.andAssertFileWasGenerated("grammar.jj");
        steps.andAssertFileWasGenerated("HelloTreeConstants.java");
        steps.andAssertFileWasGenerated("JJTHelloState.java");
        steps.andAssertFileWasGenerated("Node.java");
        steps.andAssertFileWasGenerated("SimpleNode.java");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc");
        steps.andAssertFileWasGenerated("Hello.java");
        steps.andAssertFileWasGenerated("HelloTokenManager.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileWasGenerated("TokenMgrError.java");
        steps.andAssertFileWasGenerated("HelloConstants.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("Token.java");
    }

    @Test
    public void givenASimpleJJTreeProjectThatConfiguresTheInputOutputDirectoriesWhenExecuteCompileJavaccTaskThenTheFilesAreGeneratedInTheConfiguredDirectory()
        throws URISyntaxException, IOException {
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleJJTreeTestWithConfiguredInputsOutputs");
        steps.whenTasks(CLEAN, COMPILE_JAVACC).execute();

        steps.thenAssertOutputDirectoryExists("build" + File.separator + "output");
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
    public void givenASimpleProjectWithCustomAstClassesWhenExecuteCompileJavaccTaskThenTheFilesThatDoNotHaveACorrespondingCustomAstClassAreGeneratedInTheDefaultDirectory()
        throws URISyntaxException, IOException {
        
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTestWithCustomAstClass");
        steps.whenTasks(CLEAN, COMPILE_JAVACC).execute();

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc");
        steps.andAssertFileExistsButWasNotGenerated("MyParser.java");
        steps.andAssertFileExistsButWasNotGenerated("MyParserConstants.java");
        steps.andAssertFileExistsButWasNotGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileExistsButWasNotGenerated("Token.java");
        steps.andAssertFileExistsButWasNotGenerated("TokenMgrError.java");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc" + File.separator + "test" + File.separator + "pkg");
        steps.andAssertFileWasGenerated("JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileExistsButWasNotGenerated("Token.java");
        steps.andAssertFileDoesNotExist("TokenMgrError.java");
    }
    
    @Test
    public void givenASimpleProjectWithCustomAstClassesWhenRerunCompileJavaccTaskThenTheFilesThatDoNotHaveACorrespondingCustomAstClassAreGeneratedInTheDefaultDirectory()
        throws URISyntaxException, IOException {
        
        CompilationSteps steps = new CompilationSteps();

        steps.givenAProjectNamed("simpleTestWithCustomAstClass");
        steps.whenTasks(CLEAN, COMPILE_JAVACC).execute();
        steps.whenTasks(COMPILE_JAVACC).withArguments("--rerun-tasks").execute();

        String buildDirectory = "build" + File.separator + "generated";

        steps.thenAssertOutputDirectoryDoesNotExists(buildDirectory + File.separator + "jjtree");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc");
        steps.andAssertFileExistsButWasNotGenerated("MyParser.java");
        steps.andAssertFileExistsButWasNotGenerated("MyParserConstants.java");
        steps.andAssertFileExistsButWasNotGenerated("MyParserTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileExistsButWasNotGenerated("Token.java");
        steps.andAssertFileExistsButWasNotGenerated("TokenMgrError.java");

        steps.thenAssertOutputDirectoryExists(buildDirectory + File.separator + "javacc" + File.separator + "test" + File.separator + "pkg");
        steps.andAssertFileWasGenerated("JavaccOutputTest.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestConstants.java");
        steps.andAssertFileWasGenerated("JavaccOutputTestTokenManager.java");
        steps.andAssertFileWasGenerated("ParseException.java");
        steps.andAssertFileWasGenerated("SimpleCharStream.java");
        steps.andAssertFileExistsButWasNotGenerated("Token.java");
        steps.andAssertFileDoesNotExist("TokenMgrError.java");
    }
}
