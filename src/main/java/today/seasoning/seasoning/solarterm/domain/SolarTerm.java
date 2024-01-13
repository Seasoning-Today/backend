package today.seasoning.seasoning.solarterm.domain;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import today.seasoning.seasoning.common.util.TsidUtil;

@Entity
@Getter
@NoArgsConstructor
public class SolarTerm {

    @Id
    private Long id;

    private int sequence;

    private int year;

    private int month;

    private int day;

    public SolarTerm(int sequence, int year, int month, int day) {
        this.id = TsidUtil.createLong();
        this.sequence = sequence;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public SolarTerm(int sequence, LocalDate date) {
        this.id = TsidUtil.createLong();
        this.sequence = sequence;
        this.year = date.getYear();
        this.month = date.getMonthValue();
        this.day = date.getDayOfMonth();
    }
}
