package com.icw.esign.exception;

public class DocumentException extends Exception {
    private static final long serialVersionUID = 1L;

    private boolean inputError;

    public DocumentException(boolean inputError, String errorMessage) {
        super(errorMessage);
        this.inputError = inputError;
    }

    public boolean isInputError() {
        return inputError;
    }
}
