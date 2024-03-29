package today.seasoning.seasoning.article.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, CustomArticleRepository {

	@Query("SELECT a From Article a WHERE a.user.id = :userId AND a.createdYear = :year")
	List<Article> findByUserIdAndYear(@Param("userId") Long userId, @Param("year") int year);

	@Query("SELECT a From Article a WHERE a.user.id = :userId AND a.createdTerm = :term ORDER BY a.id DESC")
	List<Article> findByTerm(@Param("userId") Long userId, @Param("term") int term);

	@Query("SELECT COUNT(a) > 0 FROM Article a WHERE a.user.id = :userId AND a.createdYear = :year AND a.createdTerm = :term")
	boolean checkArticleRegistered(@Param("userId") Long userId, @Param("year") int year, @Param("term") int term);

	@Query(value = "SELECT * FROM article a " +
		"INNER JOIN friendship f ON a.user_id = f.user_id AND f.friend_id = :userId " +
		"AND a.published = true " +
		"AND a.id < :articleId " +
		"ORDER BY a.id DESC " +
		"LIMIT :size", nativeQuery = true)
	List<Article> findFriendArticles(@Param("userId") Long userId, @Param("articleId")Long lastArticleId, @Param("size") int pageSize);

	@Query(value = "SELECT * FROM article WHERE user_id = :userId AND id < :articleId ORDER BY id DESC LIMIT :size", nativeQuery = true)
	List<Article> findByPage(@Param("userId") Long userId, @Param("articleId") Long lastArticleId, @Param("size") int pageSize);

}
