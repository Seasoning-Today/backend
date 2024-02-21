package today.seasoning.seasoning.article.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleImage;
import today.seasoning.seasoning.article.dto.FindMyFriendsArticlesResult;
import today.seasoning.seasoning.article.dto.FriendArticleDto;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.user.dto.UserProfileResponse;

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
        UserProfileResponse userProfileResponse = UserProfileResponse.build(article.getUser());
        FriendArticleDto friendArticleDto = createFriendArticleDto(article);

        return new FindMyFriendsArticlesResult(userProfileResponse, friendArticleDto);
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

        try {
            StringBuilder preview = new StringBuilder();

            ContentUnit[] contentUnits = new ObjectMapper().readValue(contents, ContentUnit[].class);

            for (ContentUnit contentUnit : contentUnits) {
                preview.append(contentUnit.text.replace("\n", "")).append(" ");
            }

            return preview.substring(0, Math.min(preview.length() - 1, 100));
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Getter
    @Setter
    private static class ContentUnit {

        private String type;
        private String text;
    }
}
