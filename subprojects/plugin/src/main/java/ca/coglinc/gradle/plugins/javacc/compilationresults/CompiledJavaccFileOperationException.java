package ca.coglinc.gradle.plugins.javacc.compilationresults;

public class CompiledJavaccFileOperationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CompiledJavaccFileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
