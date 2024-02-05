package today.seasoning.seasoning.user.service;

import com.github.f4b6a3.tsid.TsidCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        String oldProfileFilename = user.getProfileImageFilename();

        verifyAccountId(command, user.getAccountId());

        UploadFileInfo uploadFileInfo = uploadProfileImage(command.getProfileImage());

        user.updateProfile(command.getAccountId(),
            command.getNickname(),
            uploadFileInfo.getFilename(),
            uploadFileInfo.getUrl());

        userRepository.save(user);

        s3Service.deleteFile(oldProfileFilename);
    }

    private void verifyAccountId(UpdateUserProfileCommand command, String currentAccountId) {
        String newAccountId = command.getAccountId();

        if (!newAccountId.equals(currentAccountId) && userRepository.existsByAccountId(newAccountId)) {
            throw new CustomException(HttpStatus.CONFLICT, "아이디 중복");
        }
    }

    private UploadFileInfo uploadProfileImage(MultipartFile profileImage) {
        String uid = TsidCreator.getTsid().encode(62);
        String originalFilename = profileImage.getOriginalFilename();
        String uploadFileName = "user/profile/" + uid + "/" + originalFilename;

        String imageUrl = s3Service.uploadFile(profileImage, uploadFileName);

        return new UploadFileInfo(uploadFileName, imageUrl);
    }
}
