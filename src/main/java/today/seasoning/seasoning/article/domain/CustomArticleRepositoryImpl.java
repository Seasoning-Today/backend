package today.seasoning.seasoning.article.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import today.seasoning.seasoning.common.exception.CustomException;

@Component
@RequiredArgsConstructor
public class CustomArticleRepositoryImpl implements CustomArticleRepository {

    private final EntityManager entityManager;

    @Override
    public Article findByIdOrElseThrow(Long id) throws CustomException {
        TypedQuery<Article> query = entityManager.createQuery("SELECT a FROM Article a WHERE a.id = :id", Article.class);
        query.setParameter("id", id);

        List<Article> result = query.getResultList();
        if (result.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "기록장 조회 실패");
        }

        return result.get(0);
    }
}
