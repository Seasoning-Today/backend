package today.seasoning.seasoning.article.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleImage;
import today.seasoning.seasoning.article.dto.FindMyFriendsArticlesResult;
import today.seasoning.seasoning.article.dto.FriendArticleDto;
import today.seasoning.seasoning.article.dto.UserProfileDto;
import today.seasoning.seasoning.common.util.TsidUtil;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindMyFriendsArticlesService {

	private final EntityManager entityManager;

	private final String SQL = "SELECT a FROM Article a " +
		"INNER JOIN Friendship f ON a.user.id = f.user.id AND f.friend.id = :userId " +
		"AND a.published = true " +
		"AND a.id < :articleId " +
		"ORDER BY a.id DESC";

	public List<FindMyFriendsArticlesResult> doFind(Long userId, Long articleId, Integer pageSize) {
		List<Article> filteredArticles = entityManager.createQuery(SQL, Article.class)
			.setParameter("userId", userId)
			.setParameter("articleId", articleId)
			.setMaxResults(pageSize)
			.getResultList();

		return filteredArticles.stream()
			.map(this::toDto)
			.collect(Collectors.toList());
	}

	private FindMyFriendsArticlesResult toDto(Article article) {
		UserProfileDto userProfileDto = UserProfileDto.build(article.getUser());
		FriendArticleDto friendArticleDto = createFriendArticleDto(article);

		return new FindMyFriendsArticlesResult(userProfileDto, friendArticleDto);
	}

	private FriendArticleDto createFriendArticleDto(Article article) {
		String contentsPreview = getContentsPreview(article.getContents());
		String thumbnailImageUrl = getFirstImageUrl(article.getArticleImages());

		return new FriendArticleDto(TsidUtil.toString(article.getId()),
			article.getCreatedYear(),
			article.getCreatedTerm(),
			contentsPreview,
			thumbnailImageUrl);
	}

	private String getFirstImageUrl(List<ArticleImage> images) {
		return images.stream()
			.min(Comparator.comparingInt(ArticleImage::getSequence))
			.map(ArticleImage::getUrl)
			.orElse(null);
	}

	private String getContentsPreview(String contents) {
		if (contents == null) {
			return "";
		}
		int previewLength = Math.min(contents.length(), 150);
		return contents.substring(0, previewLength);
	}
}
