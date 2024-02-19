package today.seasoning.seasoning.notice.domain;

import org.springframework.stereotype.Repository;

@Repository
public interface CustomNoticeRepository {

    Notice findByIdOrElseThrow(Long id);

}
