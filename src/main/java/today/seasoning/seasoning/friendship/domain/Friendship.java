package today.seasoning.seasoning.friendship.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import today.seasoning.seasoning.common.BaseTimeEntity;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;

@Entity
@Getter
@NoArgsConstructor
public class Friendship extends BaseTimeEntity {

	@Id
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_user_id")
	private User fromUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_user_id")
	private User toUser;

	@Column(nullable = false)
	private boolean valid;

	public Friendship(User fromUser, User toUser, boolean valid) {
		this.id = TsidUtil.createLong();
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.valid = valid;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid() {
		this.valid = true;
	}
}
