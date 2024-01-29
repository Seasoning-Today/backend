package today.seasoning.seasoning.friendship.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import today.seasoning.seasoning.common.BaseTimeEntity;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;

@Getter
@Entity
@NoArgsConstructor
public class FriendRequest extends BaseTimeEntity {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User toUser;

    public FriendRequest(User fromUser, User toUser) {
        this.id = TsidUtil.createLong();
        this.fromUser = fromUser;
        this.toUser = toUser;
    }
}
