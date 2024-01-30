package today.seasoning.seasoning.solarterm.domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import today.seasoning.seasoning.common.BaseTimeEntity;
import today.seasoning.seasoning.common.util.TsidUtil;

@Entity
@Getter
@NoArgsConstructor
public class SolarTerm extends BaseTimeEntity {

    @Id
    private Long id;

    private int sequence;

    private LocalDate date;

    public SolarTerm(int sequence, LocalDate date) {
        this.id = TsidUtil.createLong();
        this.sequence = sequence;
        this.date = date;
    }

    public int getDaysDiff(LocalDate date) {
        return (int) Math.abs(ChronoUnit.DAYS.between(this.date, date));
    }
}
