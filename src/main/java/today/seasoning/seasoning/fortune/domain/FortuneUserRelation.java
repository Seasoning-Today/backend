package today.seasoning.seasoning.fortune.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import today.seasoning.seasoning.common.BaseTimeEntity;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class FortuneUserRelation extends BaseTimeEntity {

    @Id
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "fortune_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Fortune fortune;

    public void updateFortune(Fortune fortune) {
        this.fortune = fortune;
    }

    public FortuneUserRelation(User user, Fortune fortune) {
        this.id = TsidUtil.createLong();
        this.user = user;
        this.fortune = fortune;
    }
    public LocalDate getDate() {
        if (getModifiedDate() != null)
            return getModifiedDate().toLocalDate();
        else
            return getCreatedDate().toLocalDate();
    }
}
