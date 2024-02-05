package today.seasoning.seasoning.article.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {

	@Query("SELECT a From Article a WHERE a.user.id = :userId AND a.createdYear = :year")
	List<Article> findByUserIdAndYear(@Param("userId") Long userId, @Param("year") int year);

	@Query("SELECT a From Article a WHERE a.user.id = :userId AND a.createdTerm = :term")
	List<Article> findByUserIdAndTerm(@Param("userId") Long userId, @Param("term") int term);

	@Query("SELECT COUNT(a) > 0 FROM Article a WHERE a.user.id = :userId AND a.createdYear = :year AND a.createdTerm = :term")
	boolean checkArticleRegistered(@Param("userId") Long userId, @Param("year") int year, @Param("term") int term);
}
