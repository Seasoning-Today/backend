package today.seasoning.seasoning.common.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Amazon SNS Topic으로 실행 결과를 전송할 메서드에 붙이는 어노테이션

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReportResultToSnsTopic {

    String name(); // 결과를 보고할 작업의 이름
}
