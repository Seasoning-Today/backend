package today.seasoning.seasoning.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import today.seasoning.seasoning.article.domain.ArticleImageRepository;
import today.seasoning.seasoning.common.aws.S3Service;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteUserService {

    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final ArticleImageRepository articleImageRepository;

    public void doService(Long userId) {
        User user = userRepository.findByIdOrElseThrow(userId);

        // Amazon S3 파일 삭제 : 프로필 사진
        if(StringUtils.hasLength(user.getProfileImageFilename())) {
            s3Service.deleteFile(user.getProfileImageFilename());
        }

        // Amazon S3 파일 삭제 : 기록장 사진
        articleImageRepository.findImageFileNamesByUserId(userId)
                .forEach(s3Service::deleteFile);

        userRepository.delete(user);
    }
}
