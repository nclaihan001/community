package nc.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "服务器信息")
public class ServerInfo {
    @Schema(name = "支持查询的货币")
    private List<Currency> currencies;
    @Schema(name = "支持的城市")
    private List<CityInfo> cities;
}
