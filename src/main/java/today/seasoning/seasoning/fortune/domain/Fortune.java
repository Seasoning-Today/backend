package today.seasoning.seasoning.fortune.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import today.seasoning.seasoning.common.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor
public class Fortune extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

}
