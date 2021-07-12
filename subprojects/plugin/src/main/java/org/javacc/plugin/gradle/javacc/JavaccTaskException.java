package org.javacc.plugin.gradle.javacc;

/**
 * Extends from {@link RuntimeException} because this exception is used to trapped checked exception in code not expecting it and that cannot be
 * changed.
 */
public class JavaccTaskException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public JavaccTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
