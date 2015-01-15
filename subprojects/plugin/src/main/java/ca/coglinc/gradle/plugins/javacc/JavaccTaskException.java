package ca.coglinc.gradle.plugins.javacc;

/**
 * Extends from {@link RuntimeException} because this exception is used to trapped checked exception in code not expecting it and that cannot be
 * changed.
 */
public class JavaccTaskException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public JavaccTaskException() {
        super();
    }

    public JavaccTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public JavaccTaskException(String message) {
        super(message);
    }

    public JavaccTaskException(Throwable cause) {
        super(cause);
    }

}
