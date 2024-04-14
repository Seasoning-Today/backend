package today.seasoning.seasoning.article.service;

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
import today.seasoning.seasoning.article.dto.RegisterArticleCommand;
import today.seasoning.seasoning.common.aws.S3Service;
import today.seasoning.seasoning.common.aws.UploadFileInfo;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.solarterm.domain.SolarTerm;
import today.seasoning.seasoning.solarterm.service.SolarTermService;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
public class RegisterArticleService {

    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final SolarTermService solarTermService;
    private final ArticleRepository articleRepository;
    private final ArticleImageRepository articleImageRepository;

    @Value("${ARTICLE_IMAGES_LIMIT}")
    private int ARTICLE_IMAGES_LIMIT;

    @Transactional
    public Long doService(RegisterArticleCommand command) {
        Article article = registerArticle(command);
        registerArticleImages(article, command.getImages());
        return article.getId();
    }

    private Article registerArticle(RegisterArticleCommand command) {
        User user = userRepository.findByIdOrElseThrow(command.getUserId());
        SolarTerm solarTerm = solarTermService.findRecordSolarTerm()
            .orElseThrow(() -> new CustomException(HttpStatus.FORBIDDEN, "등록 기간이 아닙니다."));

        if (articleRepository.checkArticleRegistered(user.getId(), solarTerm.getDate().getYear(), solarTerm.getSequence())) {
            throw new CustomException(HttpStatus.CONFLICT, "이미 등록되었습니다.");
        }

        return articleRepository.save(Article.build(user, command, solarTerm));
    }

    private void registerArticleImages(Article article, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return;
        }

        if (images.size() > ARTICLE_IMAGES_LIMIT) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미지 개수 초과");
        }

        int sequence = 1;
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                UploadFileInfo fileInfo = s3Service.uploadArticleImage(image);
                articleImageRepository.save(ArticleImage.build(article, fileInfo, sequence++));
            }
        }
    }
}
