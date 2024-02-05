package today.seasoning.seasoning.common.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.github.f4b6a3.tsid.TsidCreator;
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
	private String s3BucketName;

	@Value("${cloud.aws.cloudfront.distribution.url}")
	private String cloudfrontUrl;

	public UploadFileInfo uploadFile(MultipartFile multipartFile) {
		String filename = TsidCreator.getTsid().encode(62) + "/" + multipartFile.getOriginalFilename();

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());

		try {
			amazonS3.putObject(s3BucketName, filename, multipartFile.getInputStream(), metadata);
			return new UploadFileInfo(filename, cloudfrontUrl.concat(filename));
		} catch (Exception e) {
			log.error("Uploading File Failed : {} - {}", filename, e.getMessage());
			throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 실패");
		}
	}

	public void deleteFile(String filename) {
		try {
			amazonS3.deleteObject(s3BucketName, filename);
		} catch (Exception e) {
			log.error("Deleting File Failed : {} - {}", filename, e.getMessage());
		}
	}
}