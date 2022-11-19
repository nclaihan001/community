package nc.model.view;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Schema(description = "基础视图")
@AllArgsConstructor
public abstract class View {
    @Schema(description = "是否成功")
    private final boolean success;
    @Schema(description = "消息载体")
    private final String message;
}
