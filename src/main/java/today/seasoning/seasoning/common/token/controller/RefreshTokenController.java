package today.seasoning.seasoning.common.token.controller;


import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import today.seasoning.seasoning.common.token.domain.TokenInfo;
import today.seasoning.seasoning.common.token.dto.RefreshTokenDto;
import today.seasoning.seasoning.common.token.service.RefreshTokenService;

@RestController
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping("/refresh")
    public ResponseEntity<TokenInfo> refreshToken(@Valid @RequestBody RefreshTokenDto dto) {
        TokenInfo tokenInfo = refreshTokenService.refresh(dto.getRefreshToken());
        return ResponseEntity.ok(tokenInfo);
    }
}
