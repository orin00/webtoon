package com.webtoon.service;

import io.jsonwebtoken.Claims;

public interface JwtServiceImpl {
	String getToken(String key, Object value);
	Claims getClaims(String token);
	boolean isValid(String token);
	int getId(String token);
}