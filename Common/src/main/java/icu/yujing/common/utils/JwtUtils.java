package icu.yujing.common.utils;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.Map;

/**
 * @author: Cyqurt
 * @date: 2022/4/15
 * @version: 1.0
 * @description:
 */
public class JwtUtils {

    private static final long THE_MILL_OF_24HOURS = 1000 * 60 * 60 * 24;

    public static String createJwt(String key, Object value, String signature) {
        JwtBuilder jwtBuilder = Jwts.builder();
        return jwtBuilder
                // header
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                // preload
                .claim(key, value)
                // signature
                .signWith(SignatureAlgorithm.HS256, signature)
                // 默认过期时间为24小时
                .setExpiration(new Date(System.currentTimeMillis() + THE_MILL_OF_24HOURS))
                .compact();
    }

    public static String createJwt(String key, Object value, String signature, Date expiration) {
        JwtBuilder jwtBuilder = Jwts.builder();
        return jwtBuilder
                // header
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                // preload
                .claim(key, value)
                // signature
                .signWith(SignatureAlgorithm.HS256, signature)
                .setExpiration(expiration)
                .compact();
    }


    public static String createJwt(Map<String, Object> preloadMap, String signature) {
        JwtBuilder jwtBuilder = Jwts.builder();
        return jwtBuilder
                // header
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                // preload
                .setClaims(preloadMap)
                // signature
                .signWith(SignatureAlgorithm.HS256, signature)
                // 默认过期时间为24小时
                .setExpiration(new Date(System.currentTimeMillis() + THE_MILL_OF_24HOURS))
                .compact();
    }

    public static String createJwt(Map<String, Object> preloadMap, String signature, Date expiration) {
        JwtBuilder jwtBuilder = Jwts.builder();
        return jwtBuilder
                // header
                .setHeaderParam("alg", "HS256")
                .setHeaderParam("typ", "JWT")
                // preload
                .setClaims(preloadMap)
                // signature
                .signWith(SignatureAlgorithm.HS256, signature)
                .setExpiration(expiration)
                .compact();
    }

    public static Claims parseJwt(String signature, String jwt) {
        JwtParser jwtParser = Jwts.parser();
        Jws<Claims> jws = jwtParser.setSigningKey(signature).parseClaimsJws(jwt);
        return jws.getBody();
    }

}
