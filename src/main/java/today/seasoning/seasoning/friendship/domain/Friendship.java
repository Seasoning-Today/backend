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
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "friend_id")
	private User friend;

	@Column(nullable = false)
	private boolean valid;

	public Friendship(User user, User friend, boolean valid) {
		this.id = TsidUtil.createLong();
		this.user = user;
		this.friend = friend;
		this.valid = valid;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid() {
		this.valid = true;
	}
}
