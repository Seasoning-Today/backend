package today.seasoning.seasoning.article.service;

public interface ValidateArticleLikePolicy {

    boolean validate(Long userId, Long articleId);
}
