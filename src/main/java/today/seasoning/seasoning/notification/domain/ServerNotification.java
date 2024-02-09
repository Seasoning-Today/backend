package today.seasoning.seasoning.notification.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import today.seasoning.seasoning.common.BaseTimeEntity;
import today.seasoning.seasoning.common.util.TsidUtil;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServerNotification extends BaseTimeEntity {

    @Id
    private Long id;

    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    @Column(name = "is_read")
    private boolean read;

    @Builder
    public ServerNotification(Long userId, NotificationType type, String message, boolean read) {
        this.id = TsidUtil.createLong();
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.read = read;
    }

    public void markAsRead() {
        this.read = true;
    }
}
