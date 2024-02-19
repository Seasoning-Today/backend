package today.seasoning.seasoning.notice.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import today.seasoning.seasoning.common.exception.CustomException;

@Component
@RequiredArgsConstructor
public class CustomNoticeRepositoryImpl implements CustomNoticeRepository {

    private final EntityManager entityManager;

    @Override
    public Notice findByIdOrElseThrow(Long id) {
        TypedQuery<Notice> query = entityManager.createQuery("SELECT n FROM Notice n WHERE n.id = :id", Notice.class);
        query.setParameter("id", id);

        List<Notice> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "User Not Found");
        }

        return resultList.get(0);
    }
}
