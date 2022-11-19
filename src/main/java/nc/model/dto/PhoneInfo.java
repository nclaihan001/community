package nc.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PhoneInfo {
    @Schema(name = "名称")
    private String name;
    @Schema(name = "电话")
    private String phone;
}
