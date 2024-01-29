package today.seasoning.seasoning.article.domain;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleImageRepository extends JpaRepository<ArticleImage, Long> {

    @Query("SELECT i.filename FROM ArticleImage i WHERE i.article.user.id = :userId")
    Set<String> findImageFileNamesByUserId(@Param("userId") Long userId);
}
