package nc.model.view;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
@Schema(description = "列表对象视图")
public class ListView<E> extends ObjectView<List<E>>{
    @Schema(description = "是否为空")
    private final boolean empty;
    public ListView(List<E> data) {
        super(data);
        empty = Optional.ofNullable(data).orElseGet(List::of).isEmpty();
    }
}
