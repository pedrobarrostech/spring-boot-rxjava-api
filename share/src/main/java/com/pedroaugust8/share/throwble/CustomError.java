package com.pedroaugust8.share.throwble;

public enum CustomError {

    BAD_USER(1, ErrorType.AUTHENTICATION),
    DATABASE(2, ErrorType.EXTERNAL_SERVICES);

    private final int code;

    private final ErrorType category;

    CustomError(int code, ErrorType category) {
        this.code = code;
        this.category = category;
    }

    public int getCode() {
        return code;
    }

    public ErrorType getCategory() {
        return category;
    }
}
