package today.seasoning.seasoning.solarterm.controller;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class YearDto {

    @NotNull @Min(2024) @Max(2100)
    Integer year;

}
