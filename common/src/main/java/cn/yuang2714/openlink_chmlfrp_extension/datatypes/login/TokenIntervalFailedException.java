package cn.yuang2714.openlink_chmlfrp_extension.datatypes.login;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

public class TokenIntervalFailedException extends RuntimeException {
    public Cause reason;
    
    public TokenIntervalFailedException(Cause reason) {
        this.reason = reason;
    }
    
    public enum Cause {
        AUTHORIZATION_PENDING,
        SLOW_DOWN,
        EXPIRED_TOKEN,
        ACCESS_DENIED,
        UNKNOWN
    }
    
    @Override
    public String toString() {
        return super.toString() + " (reason: " + reason + ")";
    }
}
