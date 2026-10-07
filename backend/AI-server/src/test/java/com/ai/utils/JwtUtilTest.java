package com.ai.utils;

import com.ai.constant.JwtClaimsConstant;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtUtilTest {
    @Test
    void tokenRoundTripPreservesServerControlledUserIdentity() {
        String secret = "test-secret-that-is-long-enough-for-hmac-signing";
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, 42L);
        claims.put(JwtClaimsConstant.USERNAME, "teacher");

        String token = JwtUtil.createJWT(secret, 60_000, claims);
        Claims parsed = JwtUtil.parseJWT(secret, token);

        assertEquals("42", parsed.get(JwtClaimsConstant.USER_ID).toString());
        assertEquals("teacher", parsed.get(JwtClaimsConstant.USERNAME));
    }
}
