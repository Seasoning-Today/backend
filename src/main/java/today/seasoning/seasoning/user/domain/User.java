package today.seasoning.seasoning.user.domain;

import com.github.f4b6a3.tsid.TsidCreator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import today.seasoning.seasoning.common.BaseTimeEntity;
import today.seasoning.seasoning.common.aws.UploadFileInfo;
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

    @ColumnDefault("USER")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @ColumnDefault("true")
    @Column(nullable = false)
    private boolean searchable;

    public User(String nickname, String profileImageUrl, String email, LoginType loginType) {
        this.id = TsidCreator.getTsid().toLong();
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.accountId = TsidCreator.getTsid().toString().toLowerCase(); // 최초 랜덤값 부여
        this.email = email;
        this.loginType = loginType;
        this.role = Role.USER;
        this.searchable = true;
    }

    public void updateProfile(String nickname, String accountId) {
        this.nickname = nickname;
        this.accountId = accountId;
    }

    public void removeProfileImage() {
        this.profileImageUrl = null;
        this.profileImageFilename = null;
    }

    public void changeProfileImage(UploadFileInfo uploadFile) {
        this.profileImageFilename = uploadFile.getFilename();
        this.profileImageUrl = uploadFile.getUrl();
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }
}
