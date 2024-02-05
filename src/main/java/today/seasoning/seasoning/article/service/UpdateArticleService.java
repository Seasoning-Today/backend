package today.seasoning.seasoning.article.service;

import java.util.List;
import java.util.stream.Stream;
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

        checkRequestValid(article, command);

        article.update(command.isPublished(), command.getContents());

        if (command.isImageModified()) {
            Stream<String> oldImageFilenames = article.getArticleImages().stream().map(ArticleImage::getFilename);
            updateArticleImages(article, command.getImages());
            oldImageFilenames.forEach(s3Service::deleteFile);
        }
    }

    private void checkRequestValid(Article article, UpdateArticleCommand command) {
        solarTermService.findRecordSolarTerm()
            .orElseThrow(() -> new CustomException(HttpStatus.FORBIDDEN, "등록 기간이 아닙니다."));

        Long ownerId = article.getUser().getId();
        if (!ownerId.equals(command.getUserId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "권한 없음");
        }
    }

    private void updateArticleImages(Article article, List<MultipartFile> images) {
        article.getArticleImages().clear();

        if (images == null || images.isEmpty()) {
            return;
        }

        if (images.size() > ARTICLE_IMAGES_LIMIT) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미지 개수 초과");
        }

        int sequence = 1;
        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) {
                continue;
            }
            UploadFileInfo fileInfo = s3Service.uploadFile(image);
            ArticleImage articleImage = ArticleImage.build(article, fileInfo, sequence++);
            articleImageRepository.save(articleImage);
        }
    }
}
