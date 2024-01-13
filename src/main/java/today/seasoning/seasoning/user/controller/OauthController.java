package today.seasoning.seasoning.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import today.seasoning.seasoning.user.dto.LoginResponse;
import today.seasoning.seasoning.user.dto.LoginResult;
import today.seasoning.seasoning.user.service.kakao.KakaoLoginService;

@RestController
@RequiredArgsConstructor
public class OauthController {

    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/oauth/login/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestParam("code") String authCode) {
        LoginResult loginResult = kakaoLoginService.handleKakaoLogin(authCode);
        LoginResponse loginResponse = LoginResponse.build(loginResult);
        return ResponseEntity.ok(loginResponse);
    }
}
