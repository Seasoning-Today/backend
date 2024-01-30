package today.seasoning.seasoning.article.service;

import com.github.f4b6a3.tsid.TsidCreator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import today.seasoning.seasoning.article.domain.Article;
import today.seasoning.seasoning.article.domain.ArticleImage;
import today.seasoning.seasoning.article.domain.ArticleImageRepository;
import today.seasoning.seasoning.article.domain.ArticleRepository;
import today.seasoning.seasoning.article.dto.UpdateArticleCommand;
import today.seasoning.seasoning.common.aws.S3Service;
import today.seasoning.seasoning.common.aws.UploadFileInfo;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.solarterm.service.SolarTermService;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateArticleService {

    private final S3Service s3Service;
    private final SolarTermService solarTermService;
    private final ArticleRepository articleRepository;
    private final ArticleImageRepository articleImageRepository;

    @Value("${ARTICLE_IMAGES_LIMIT}")
    private int ARTICLE_IMAGES_LIMIT;

    public void doUpdate(UpdateArticleCommand command) {
        Article article = articleRepository.findById(command.getArticleId())
            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "기록장 조회 실패"));

        solarTermService.findRecordSolarTerm()
            .orElseThrow(() -> new CustomException(HttpStatus.FORBIDDEN, "등록 기간이 아닙니다."));

        Long ownerId = article.getUser().getId();
        if (!ownerId.equals(command.getUserId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "권한 없음");
        }

        deleteOldImages(article.getArticleImages());

        uploadAndRegisterArticleImages(article, command.getImages());

        updateArticle(article, command);
    }

    private void deleteOldImages(List<ArticleImage> articleImages) {
        articleImages.stream()
            .map(ArticleImage::getFilename)
            .forEach(s3Service::deleteFile);
        articleImages.clear();
    }

    private void uploadAndRegisterArticleImages(Article article, List<MultipartFile> images) {
        if (images.size() > ARTICLE_IMAGES_LIMIT) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미지 개수 초과");
        }

        for (int sequence = 0; sequence < images.size(); sequence++) {
            MultipartFile image = images.get(sequence);
            UploadFileInfo uploadFileInfo = uploadImage(image);
            registerArticleImage(article, uploadFileInfo, sequence + 1);
        }
    }

    private UploadFileInfo uploadImage(MultipartFile image) {
        String uid = TsidCreator.getTsid().encode(62);
        String uploadFileName = "images/article/" + uid + "/" + image.getOriginalFilename();
        String url = s3Service.uploadFile(image, uploadFileName);
        return new UploadFileInfo(uploadFileName, url);
    }

    private void registerArticleImage(Article article, UploadFileInfo fileInfo, int sequence) {
        ArticleImage articleImage = new ArticleImage(TsidUtil.createLong(),
            article,
            fileInfo.getFilename(),
            fileInfo.getUrl(),
            sequence);
        articleImageRepository.save(articleImage);
    }

    private void updateArticle(Article article, UpdateArticleCommand command) {
        article.update(command.isPublished(), command.getContents());
        articleRepository.save(article);
    }
}
