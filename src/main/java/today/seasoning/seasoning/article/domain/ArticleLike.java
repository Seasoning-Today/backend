package today.seasoning.seasoning.article.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import today.seasoning.seasoning.common.BaseTimeEntity;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "article_like",
	uniqueConstraints = {@UniqueConstraint(columnNames = {"article_id", "user_id"})})
public class ArticleLike extends BaseTimeEntity {

	@Id
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "article_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Article article;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public ArticleLike(Article article, User user) {
		this.id = TsidUtil.createLong();
		this.article = article;
		this.user = user;
	}
}
