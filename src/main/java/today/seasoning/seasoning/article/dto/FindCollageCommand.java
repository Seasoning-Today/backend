package today.seasoning.seasoning.article.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import today.seasoning.seasoning.common.exception.CustomException;

@Getter
public class FindCollageCommand {

    private final Long userId;
    private final int year;

    public FindCollageCommand(Long userId, int year) {
        if (year < 2023 || year > 2100) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "조회 년도 오류");
        }
        this.userId = userId;
        this.year = year;
    }
}
