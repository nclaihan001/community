package nc.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ExchangeRateHistory {
    @Schema(description = "更新时间")
    private LocalDate date;
    @Schema(description = "汇率")
    private String rate;
}
