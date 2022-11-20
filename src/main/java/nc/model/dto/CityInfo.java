package nc.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CityInfo {
    @Schema(name = "城市ID")
    private int id;
    @Schema(name = "城市名称")
    private String name;
}
