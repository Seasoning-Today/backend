package today.seasoning.seasoning.common.aws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnsService {

    @Value("${cloud.aws.sns.arn}")
    private String topicArn;

    private final SnsClient snsClient;

    public void publish(String message) {
        try {
            PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .messageGroupId("groupId")
                .message(message)
                .build();

            PublishResponse response = snsClient.publish(request);
            log.info("SNS Message Published - Id:{}", response.messageId());
        } catch (Exception e) {
            log.error("SNS Message Publish Failed : {}", e.getMessage());
        }
    }

}
