package com.hammer.lei.enums;

public enum ProfileBitFlag {
    NOCHANGE(0),
    CHANGE(190);

    private final int code;

    ProfileBitFlag(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
