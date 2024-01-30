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
import today.seasoning.seasoning.common.token.domain.TokenInfo;
import today.seasoning.seasoning.common.token.domain.TokenProperties;

@Component
public class JwtUtil {

    private static SecretKey secretKey;
    private static TokenProperties tokenProperties;

    @Autowired
    private JwtUtil(TokenProperties tokenProperties) {
        JwtUtil.tokenProperties = tokenProperties;
        JwtUtil.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenProperties.getSecretKey()));
    }

    // 새로운 액세스 토큰 및 리프레시 토큰 생성
    public static TokenInfo createToken(long userId) {
        return new TokenInfo(generateAccessToken(userId), generateRefreshToken(userId));
    }

    // 리프레시 토큰을 통한 액세스 토큰 재발급
    public static TokenInfo refreshToken(String refreshToken) {
        Claims claims = getClaims(refreshToken);
        Long userId = TsidUtil.toLong(claims.get("uid", String.class));
        return new TokenInfo(generateAccessToken(userId), null);
    }

    // 액세스 토큰 생성
    private static String generateAccessToken(Long userId) {
        ClaimsBuilder claimsBuilder = Jwts.claims();
        claimsBuilder.subject(TsidUtil.toString(userId));

        return Jwts.builder()
            .claims(claimsBuilder.build())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + tokenProperties.getAccessTokenExpirationTimeMillis()))
            .signWith(secretKey, SIG.HS256)
            .compact();
    }

    // 리프레시 토큰 생성
    private static String generateRefreshToken(long userId) {
        ClaimsBuilder claimsBuilder = Jwts.claims();
        claimsBuilder.add("uid", TsidUtil.toString(userId));

        return Jwts.builder()
            .claims(claimsBuilder.build())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + tokenProperties.getRefreshTokenExpirationTimeMillis()))
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
        return TsidUtil.toLong(getClaims(token).getSubject());
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
