package today.seasoning.seasoning.common.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import today.seasoning.seasoning.common.aws.SnsService;

@Aspect
@Component
@RequiredArgsConstructor
public class ReportResultToSnsTopicAspect {

    private final SnsService snsService;

    @Pointcut("@annotation(reportResultToSnsTopic)")
    public void reportResultToSnsTopicPointCut(ReportResultToSnsTopic reportResultToSnsTopic) {
    }

    @AfterReturning(value = "reportResultToSnsTopicPointCut(reportResultToSnsTopic)")
    public void reportSuccessResult(ReportResultToSnsTopic reportResultToSnsTopic) {
        snsService.publish("[시즈닝] " + reportResultToSnsTopic.name() + " 성공");
    }

    @AfterThrowing(value = "reportResultToSnsTopicPointCut(reportResultToAmazonSnsTopic)", throwing = "exception")
    public void reportFailureResult(ReportResultToSnsTopic reportResultToAmazonSnsTopic, Exception exception) {
        snsService.publish("[시즈닝] " + reportResultToAmazonSnsTopic.name() + " 실패");
    }
}
