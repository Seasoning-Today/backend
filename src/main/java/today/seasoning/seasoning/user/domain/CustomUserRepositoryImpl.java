package today.seasoning.seasoning.user.domain;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import today.seasoning.seasoning.common.exception.CustomException;

@Component
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository{

    private final EntityManager entityManager;

    @Override
    public User findByIdOrElseThrow(Long id) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class);
        query.setParameter("id", id);

        List<User> resultList = query.getResultList();
        if (resultList.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "User Not Found");
        }

        return resultList.get(0);
    }
}
