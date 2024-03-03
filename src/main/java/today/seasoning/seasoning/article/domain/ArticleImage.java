package today.seasoning.seasoning.article.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import today.seasoning.seasoning.common.BaseTimeEntity;
import today.seasoning.seasoning.common.aws.UploadFileInfo;
import today.seasoning.seasoning.common.util.TsidUtil;

@Entity
@Getter
@NoArgsConstructor
public class ArticleImage extends BaseTimeEntity {

	@Id
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "article_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Article article;

	@Column(nullable = false)
	private String filename;

	@Column(nullable = false)
	private String url;

	@Check(constraints = "sequence >= 1")
	@Column(nullable = false)
	private int sequence;

	public ArticleImage(Article article, String filename, String url, int sequence) {
		this.id = TsidUtil.createLong();
		this.article = article;
		this.filename = filename;
		this.url = url;
		this.sequence = sequence;
	}

	public static ArticleImage build(Article article, UploadFileInfo image, int sequence) {
		return new ArticleImage(article, image.getFilename(), image.getUrl(), sequence);
	}
}
