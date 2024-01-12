package today.seasoning.seasoning.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import today.seasoning.seasoning.common.token.domain.TokenProperties;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.common.token.domain.TokenInfo;

@Component
public class JwtUtil {

    private static TokenProperties tokenProperties;
    private static SecretKey secretKey;

    @Autowired
    private JwtUtil(TokenProperties tokenProperties) {
        JwtUtil.tokenProperties = tokenProperties;
        JwtUtil.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenProperties.getSecretKey()));
    }

    // 새로운 액세스 토큰 및 리프레시 토큰 생성
    public static TokenInfo createToken(long userId, LoginType loginType) {
        String accessToken = generateAccessToken(userId, loginType);
        String refreshToken = generateRefreshToken(loginType);
        return new TokenInfo(accessToken, refreshToken);
    }

    // 리프레시 토큰을 통한 액세스 토큰 및 리프레시 토큰 재발급
    // 리프레시 토큰 탈취 피해를 줄이기 위해 리프레시 토큰도 재생성 (만료시간은 유지)
    public static TokenInfo refreshToken(long userId, String refreshToken) {
        Claims claims = getClaims(refreshToken);
        LoginType loginType = LoginType.valueOf(claims.get("loginType", String.class));
        Date refreshTokenExpirationDate = claims.getExpiration();

        String accessToken = generateAccessToken(userId, loginType);
        String newRefreshToken = regenerateRefreshToken(loginType, refreshTokenExpirationDate.getTime());
        return new TokenInfo(accessToken, newRefreshToken);
    }

    // 액세스 토큰 생성
    private static String generateAccessToken(Long userId, LoginType loginType) {
        return generateToken(userId, loginType,
            System.currentTimeMillis() + tokenProperties.getAccessTokenExpirationTimeMillis());
    }

    // 리프레시 토큰 생성
    private static String generateRefreshToken(LoginType loginType) {
        return generateToken(null, loginType,
            System.currentTimeMillis() + tokenProperties.getRefreshTokenExpirationTimeMillis());
    }

    // 리프레시 토큰 재발급 (만료시간은 유지)
    private static String regenerateRefreshToken(LoginType loginType, long expirationTimeMillis) {
        return generateToken(null, loginType, expirationTimeMillis);
    }

    private static String generateToken(Long userId, LoginType loginType, long expirationTimeMillis) {
        ClaimsBuilder claimsBuilder = Jwts.claims();
        claimsBuilder.add("loginType", loginType.name());

        // 리프레시 토큰은 subject 설정 X
        if (userId != null) {
            claimsBuilder.subject(TsidUtil.toString(userId));
        }

        return Jwts.builder()
            .claims(claimsBuilder.build())
            .issuedAt(new Date())
            .expiration(new Date(expirationTimeMillis))
            .signWith(secretKey, SIG.HS256)
            .compact();
    }

    private static Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public static long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    public static LoginType getLoginType(String token) {
        return LoginType.valueOf((getClaims(token).get("loginType", String.class)));
    }

    // 토큰 유효성 검증 메서드
    public static boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}