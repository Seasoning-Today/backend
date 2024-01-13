package today.seasoning.seasoning.user.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import today.seasoning.seasoning.common.BaseTimeEntity;
import today.seasoning.seasoning.common.enums.LoginType;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseTimeEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(name = "profile_image_filename")
    private String profileImageFilename;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(unique = true, nullable = false)
    private String accountId;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String nickname, String profileImageUrl, String email, LoginType loginType) {
        this.id = TsidCreator.getTsid().toLong();
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.accountId = TsidCreator.getTsid().toString(); // 최초 랜덤값 부여
        this.email = email;
        this.loginType = loginType;
        this.role = Role.USER;
    }

    public void updateProfile(String accountId, String nickname, String profileImageFileName, String profileImageUrl) {
        this.accountId = accountId;
        this.nickname = nickname;
        this.profileImageFilename = profileImageFileName;
        this.profileImageUrl = profileImageUrl;
    }
}
