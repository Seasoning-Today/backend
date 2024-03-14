package today.seasoning.seasoning.article.domain;

import today.seasoning.seasoning.common.exception.CustomException;

public interface CustomArticleRepository {

    Article findByIdOrElseThrow(Long id) throws CustomException;

}
