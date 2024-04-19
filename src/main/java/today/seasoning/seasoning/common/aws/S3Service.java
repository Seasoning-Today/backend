package today.seasoning.seasoning.common.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import today.seasoning.seasoning.common.exception.CustomException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String BUCKET_NAME;

	@Value("${cloud.aws.cloudfront.distribution.url}")
	private String CLOUDFRONT_DISTRIBUTION_URL;

	@Value("${cloud.aws.s3.image.prefix.original}")
	private String ORIGINAL_PREFIX;

	@Value("${cloud.aws.s3.image.prefix.resized}")
	private String RESIZED_PREFIX;

	@Value("${cloud.aws.s3.image.prefix.profile}")
	private String PROFILE_IMAGE_PREFIX;

	@Value("${cloud.aws.s3.image.prefix.article}")
	private String ARTICLE_IMAGE_PREFIX;

	public UploadFileInfo uploadProfileImage(MultipartFile multipartFile) {
		String key = buildKey(PROFILE_IMAGE_PREFIX);
		return uploadFile(multipartFile, key, true);
	}

	public UploadFileInfo uploadArticleImage(MultipartFile multipartFile) {
		String key = buildKey(ARTICLE_IMAGE_PREFIX);
		return uploadFile(multipartFile, key, false);
	}

	private UploadFileInfo uploadFile(MultipartFile multipartFile, String key, boolean resized) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());

		try {
			amazonS3.putObject(BUCKET_NAME, key, multipartFile.getInputStream(), metadata);
		} catch (Exception e) {
			log.error("S3 Upload Failed - key : {} / message : {}", key, e.getMessage());
			throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 실패");
		}

		if(resized) {
			return new UploadFileInfo(key, CLOUDFRONT_DISTRIBUTION_URL + resolveResizedObjectKey(key));
		}
		return new UploadFileInfo(key, CLOUDFRONT_DISTRIBUTION_URL + key);
	}

	public void deleteFile(String key) {
		try {
			amazonS3.deleteObject(BUCKET_NAME, key);
		} catch (Exception e) {
			log.error("S3 Delete Failed - key : {} / message : {}", key, e.getMessage());
		}
	}

	private String buildKey(String prefix) {
		return ORIGINAL_PREFIX + prefix + UUID.randomUUID();
	}

	private String resolveResizedObjectKey(String originalKey) {
		return originalKey.replaceFirst(ORIGINAL_PREFIX, RESIZED_PREFIX);
	}
}
