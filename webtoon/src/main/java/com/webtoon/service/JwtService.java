package com.webtoon.service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service("jwtService")
public class JwtService implements JwtServiceImpl {
    private byte[] secretKey = "avvvsdfesgre&&&&&ergreigh34i6h26i##4934gtogew%%%t8g94g3962g6g!!!!!t0fg46fvsdfgg34g5t82g64@@@".getBytes();

    @Override
    public String getToken(String key, Object value) {
        Date now = new Date();
        Date expTime = new Date(now.getTime() + 1000 * 60 * 60 * 2);

        Key signKey = new SecretKeySpec(secretKey, SignatureAlgorithm.HS256.getJcaName());

        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("typ", "JWT");
        headerMap.put("alg", "HS256");

        Map<String, Object> map = new HashMap<>();
        map.put(key, value);

        JwtBuilder builder = Jwts.builder()
                .setHeader(headerMap)
                .setClaims(map)
                .setIssuedAt(now)
                .setExpiration(expTime)
                .signWith(signKey, SignatureAlgorithm.HS256);

        return builder.compact();
    }
    
    @Override
    public Claims getClaims(String token) {
        if(token != null && !"".equals(token))
        {
            try {
                Key signKey = new SecretKeySpec(secretKey, SignatureAlgorithm.HS256.getJcaName());
                return Jwts.parserBuilder().setSigningKey(signKey).build().parseClaimsJws(token).getBody();
            }
            catch (ExpiredJwtException e) {
                // 만료됨
            }
            catch (JwtException e) {
                // 유효하지 않음
            }
        }
        return null;
    }
    
    @Override
    public boolean isValid(String token) {
    	return this.getClaims(token) != null;
    }
    
    @Override
    public int getId(String token) {
    	Claims claims = this.getClaims(token);
    	
    	if (claims != null) {
    		return Integer.parseInt(claims.get("id").toString());
    	}
    	return 0;
    }
}
