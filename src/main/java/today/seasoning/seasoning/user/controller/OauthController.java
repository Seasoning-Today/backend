package today.seasoning.seasoning.user.controller;

import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import today.seasoning.seasoning.user.dto.LoginResultDto;
import today.seasoning.seasoning.user.service.kakao.KakaoLoginService;

@RestController
@RequiredArgsConstructor
public class OauthController {

    private final KakaoLoginService kakaoLoginService;

    @Value("${KAKAO_LOGIN_URL}")
    private String kakaoLoginUri;

    @Value("${MAIN_PAGE_URL}")
    private String mainPageUrl;

    @Value("${MY_PAGE_URL}")
    private String myPageUrl;

    @GetMapping("/kakao/login")
    public void redirectToKakaoLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect(kakaoLoginUri);
    }

    @GetMapping("/oauth/kakao/login")
    public void kakaoLogin(@RequestParam("code") String authCode, HttpServletResponse response) throws IOException {

        LoginResultDto loginResultDto = kakaoLoginService.handleKakaoLogin(authCode);
        boolean isNewUser = loginResultDto.isNewUser();

        Cookie authorization = new Cookie("Authorization", loginResultDto.getToken());
        response.addCookie(authorization);

        String redirectUrl = isNewUser ? myPageUrl : mainPageUrl;
        response.sendRedirect(redirectUrl);
    }
}
