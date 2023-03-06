package com.gemini.generic.exception;

/**
 * <p>GemException class.</p>
 *
 * @version $Id: $
 */

public class GemException extends Exception {

    /**
     * Constant <code>serialVersionUID=1835257505622895054L</code>.
     */
    private static final long serialVersionUID = 1835257505622895054L;

    /**
     * <p>Constructor for GemException.</p>
     */
    public GemException() {
        super();
    }

    /**
     * <p>Constructor for GemException.</p>
     *
     * @param message a {@link java.lang.String} object.
     * @param cause   a {@link java.lang.Throwable} object.
     */
    public GemException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * <p>Constructor for GemException.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public GemException(final String message) {
        super(message);
    }

    /**
     * <p>Constructor for GemException.</p>
     *
     * @param cause a {@link java.lang.Throwable} object.
     */
    public GemException(final Throwable cause) {
        super(cause);
    }

    public String getMessage() {
        return super.getMessage();
    }
}


