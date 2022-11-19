package nc.model.view;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "单对象视图")
public class ObjectView<E> extends View{
    @Schema(description = "返回的实体")
    private final E data;

    public ObjectView(E data) {
        super(true, "获取成功");
        this.data = data;
    }
}
