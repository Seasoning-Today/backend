package today.seasoning.seasoning.fortune.domain;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import today.seasoning.seasoning.common.BaseTimeEntity;
import today.seasoning.seasoning.common.util.TsidUtil;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FortuneUserRelation extends BaseTimeEntity {

    @Id
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fortune_id", nullable = false)
    private Fortune fortune;

    public FortuneUserRelation(Long userId, Fortune fortune) {
        this.id = TsidUtil.createLong();
        this.userId = userId;
        this.fortune = fortune;
    }

    public void changeFortune(Fortune fortune) {
        this.fortune = fortune;
    }

    public LocalDate getLastModifiedDate() {
        return getModifiedDate().toLocalDate();
    }
}
