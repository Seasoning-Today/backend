package today.seasoning.seasoning.article.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import today.seasoning.seasoning.common.BaseTimeEntity;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.domain.User;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Article extends BaseTimeEntity {

    @Id
    @Column(name = "article_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean published;

    @Check(constraints = "created_year >= 2023 AND created_year < 2100")
    @Column(name = "created_year", nullable = false, columnDefinition = "INTEGER CHECK (created_year >= 2023 AND created_year < 2100)")
    private int createdYear;

    @Check(constraints = "created_term >= 1 AND created_term <= 24")
    @Column(name = "created_term", nullable = false, columnDefinition = "INTEGER CHECK (created_term >= 1 AND created_term <= 24)")
    private int createdTerm;

    @Lob
    private String contents;

    @OneToMany(mappedBy = "article", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<ArticleImage> articleImages;

    @OneToMany(mappedBy = "article", cascade = CascadeType.REMOVE)
    private List<ArticleLike> articleLikes;

    public Article(User user, boolean published, int createdYear, int createdTerm, String contents) {
        this.id = TsidUtil.createLong();
        this.user = user;
        this.published = published;
        this.createdYear = createdYear;
        this.createdTerm = createdTerm;
        this.contents = contents;
    }

    public void update(boolean published, String contents) {
        this.published = published;
        this.contents = contents;
    }
}
