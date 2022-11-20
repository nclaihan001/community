package nc.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Currency {
    @Schema(name = "货币ID")
    private String id;
    @Schema(name = "货币名称")
    private String name;
}
