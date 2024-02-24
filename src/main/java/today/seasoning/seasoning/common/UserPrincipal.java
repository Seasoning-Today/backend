package today.seasoning.seasoning.common;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import today.seasoning.seasoning.common.enums.LoginType;
import today.seasoning.seasoning.user.domain.Role;
import today.seasoning.seasoning.user.domain.User;

@Getter
@Hidden
public class UserPrincipal implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String nickname;
    private final String profileImageUrl;
    private final String accountId;
    private final String email;
    private final LoginType loginType;
    private final Role role;

    public UserPrincipal(Long id, String nickname, String profileImageUrl, String accountId, String email,
        LoginType loginType, Role role) {
        this.id = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.accountId = accountId;
        this.email = email;
        this.loginType = loginType;
        this.role = role;
    }

    public static UserPrincipal build(User user) {
        return new UserPrincipal(
            user.getId(),
            user.getNickname(),
            user.getProfileImageUrl(),
            user.getAccountId(),
            user.getEmail(),
            user.getLoginType(),
            user.getRole());
    }
}
