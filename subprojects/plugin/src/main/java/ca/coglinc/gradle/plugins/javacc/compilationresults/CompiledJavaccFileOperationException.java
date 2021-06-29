package org.javacc.plugin.gradle.javacc.compilationresults;

public class CompiledJavaccFileOperationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CompiledJavaccFileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
