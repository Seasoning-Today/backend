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
import today.seasoning.seasoning.article.dto.RegisterArticleCommand;
import today.seasoning.seasoning.common.aws.S3Service;
import today.seasoning.seasoning.common.aws.UploadFileInfo;
import today.seasoning.seasoning.common.exception.CustomException;
import today.seasoning.seasoning.common.util.TsidUtil;
import today.seasoning.seasoning.solarterm.domain.SolarTerm;
import today.seasoning.seasoning.solarterm.service.SolarTermService;
import today.seasoning.seasoning.user.domain.User;
import today.seasoning.seasoning.user.domain.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterArticleService {

    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final SolarTermService solarTermService;
    private final ArticleRepository articleRepository;
    private final ArticleImageRepository articleImageRepository;

    @Value("${ARTICLE_IMAGES_LIMIT}")
    private int ARTICLE_IMAGES_LIMIT;

    public Long doRegister(RegisterArticleCommand command) {
        Article article = createArticle(command);
        articleRepository.save(article);

        uploadAndRegisterArticleImages(article, command.getImages());

        return article.getId();
    }

    private Article createArticle(RegisterArticleCommand command) {
        User user = userRepository.findById(command.getUserId()).get();

        SolarTerm solarTerm = solarTermService.findRecordSolarTerm()
            .orElseThrow(() -> new CustomException(HttpStatus.FORBIDDEN, "등록 기간이 아닙니다."));

        return new Article(user, command.isPublished(), solarTerm.getDate().getYear(),
            solarTerm.getSequence(), command.getContents());
    }

    private void uploadAndRegisterArticleImages(Article article, List<MultipartFile> images) {
        if (images.size() > ARTICLE_IMAGES_LIMIT) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미지 개수 초과");
        }

        for (int sequence = 0; sequence < images.size(); sequence++) {
            MultipartFile image = images.get(sequence);
            UploadFileInfo fileInfo = uploadImage(image);
            registerArticleImage(article, fileInfo, sequence + 1);
        }
    }

    private UploadFileInfo uploadImage(MultipartFile image) {
        String uid = TsidCreator.getTsid().encode(62);
        String originalFilename = image.getOriginalFilename();
        String uploadFileName = "images/article/" + uid + "/" + originalFilename;

        String url = s3Service.uploadFile(image, uploadFileName);

        return new UploadFileInfo(uploadFileName, url);
    }

    private void registerArticleImage(Article article, UploadFileInfo fileInfo, int sequence) {
        ArticleImage articleImage = new ArticleImage(
            TsidUtil.createLong(),
            article,
            fileInfo.getFilename(),
            fileInfo.getUrl(),
            sequence);

        articleImageRepository.save(articleImage);
    }
}
