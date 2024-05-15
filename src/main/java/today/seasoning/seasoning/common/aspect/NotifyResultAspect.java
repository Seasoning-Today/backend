package today.seasoning.seasoning.common.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import today.seasoning.seasoning.common.aws.SnsService;

@Aspect
@Component
@RequiredArgsConstructor
public class NotifyResultAspect {

    private final SnsService snsService;

    @AfterReturning(pointcut = "@annotation(notifyResult)")
    public void reportSuccessResult(NotifyResult notifyResult) {
        snsService.publish("[시즈닝] " + notifyResult.name() + " 완료");
    }

    @AfterThrowing(pointcut = "@annotation(notifyResult)")
    public void reportFailureResult(NotifyResult notifyResult) {
        snsService.publish("[시즈닝] " + notifyResult.name() + " 실패");
    }
}
