package today.seasoning.seasoning.user.service;

import com.github.f4b6a3.tsid.TsidCreator;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import today.seasoning.seasoning.common.aws.S3Service;
import today.seasoning.seasoning.common.aws.UploadFileInfo;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.user.domain.AccountId;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;
import today.seasoning.seasoning.user.dto.UpdateUserProfileCommand;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateUserProfileService {

	private final S3Service s3Service;
	private final UserRepository userRepository;
	private final VerifyAccountIdService verifyAccountIdService;

	@Transactional
	public void doUpdate(UpdateUserProfileCommand command) {
		User user = userRepository.findById(command.getUserId()).get();

		validateNicknameAndAccountId(command, user.getAccountId());

		deleteOldProfileImage(user.getProfileImageFilename());

		UploadFileInfo uploadFileInfo = uploadProfileImage(command.getProfileImage());

		user.updateProfile(command.getAccountId(), command.getNickname(),
			uploadFileInfo.getFilename(),
			uploadFileInfo.getUrl());

		userRepository.save(user);
	}

	private void validateNicknameAndAccountId(UpdateUserProfileCommand command, String currentAccountId) {
		validateNicknameFormat(command.getNickname());

		String newAccountId = command.getAccountId();
		if (!newAccountId.equals(currentAccountId)) {
			verifyAccountIdService.verify(new AccountId(command.getAccountId()));
		}
	}

	private void validateNicknameFormat(String nickname) {
		if (!Pattern.matches("^[a-zA-Z0-9가-힣]{2,10}$", nickname)) {
			throw new CustomException(HttpStatus.BAD_REQUEST, "사용할 수 없는 닉네임 입니다.");
		}
	}

	private UploadFileInfo uploadProfileImage(MultipartFile profileImage) {
		String uid = TsidCreator.getTsid().encode(62);
		String originalFilename = profileImage.getOriginalFilename();
		String uploadFileName = "user/profile/" + uid + "/" + originalFilename;

		String imageUrl = s3Service.uploadFile(profileImage, uploadFileName);

		return new UploadFileInfo(uploadFileName, imageUrl);
	}

	private void deleteOldProfileImage(String filename) {
		s3Service.deleteFile(filename);
	}
}
