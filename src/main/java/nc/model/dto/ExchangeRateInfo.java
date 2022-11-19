package nc.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import nc.model.po.ExchangeType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ExchangeRateInfo {
    @Schema(description = "目标币种")
    private ExchangeType to;
    @Schema(description = "汇率")
    private String rate;
    @Schema(description = "更新时间")
    private LocalDateTime date;
    @Schema(description = "历史记录")
    private List<ExchangeRateHistory> histories;
}
