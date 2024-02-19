package today.seasoning.seasoning.notice.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import today.seasoning.seasoning.common.BaseTimeEntity;
import today.seasoning.seasoning.common.util.TsidUtil;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseTimeEntity {

    @Id
    private Long id;

    private String content;

    public Notice(String content) {
        this.id = TsidUtil.createLong();
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
