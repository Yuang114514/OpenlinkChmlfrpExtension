package cn.yuang2714.openlink_chmlfrp_extension.datatypes.login;

/*
 * Copyright (c) Yuang2714(鬝豭鶬鶬) 2026
 * Open source with MIT licence
 */

public record IntervalledAccessToken(String accessToken, String refreshToken, long expiresIn) {}
