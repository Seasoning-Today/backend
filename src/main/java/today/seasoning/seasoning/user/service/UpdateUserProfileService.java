package today.seasoning.seasoning.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import today.seasoning.seasoning.common.aws.S3Service;
import today.seasoning.seasoning.common.aws.UploadFileInfo;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;
import today.seasoning.seasoning.user.dto.UpdateUserProfileCommand;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateUserProfileService {

    private final S3Service s3Service;
    private final UserRepository userRepository;

    @Transactional
    public void doUpdate(UpdateUserProfileCommand command) {
        User user = userRepository.findByIdOrElseThrow(command.getUserId());
        String nickname = command.getNickname();
        String accountId = command.getAccountId();

        if (!user.getAccountId().equals(accountId) && userRepository.existsByAccountId(accountId)) {
            throw new CustomException(HttpStatus.CONFLICT, "아이디 중복");
        }
        user.updateProfile(nickname, accountId);

        if (command.isImageModified()) {
            changeProfileImage(user, command.getProfileImage());
        }
    }

    private void changeProfileImage(User user, MultipartFile image) {
        String currentImageFilename = user.getProfileImageFilename();

        if (image == null || image.isEmpty()) {
            user.removeProfileImage();
        } else {
            UploadFileInfo uploadFileInfo = s3Service.uploadFile(image);
            user.changeProfileImage(uploadFileInfo);
        }

        // 기존에 프로필 이미지가 존재한 경우, 이를 삭제
        if (StringUtils.hasLength(currentImageFilename)) {
            s3Service.deleteFile(currentImageFilename);
        }
    }
}
