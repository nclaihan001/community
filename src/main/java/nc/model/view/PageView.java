package nc.model.view;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "列表对象视图")
public class PageView<E> extends ListView<E>{
    @Schema(description = "总行数")
    private final long elements;
    @Schema(description = "总页数")
    private final long pages;
    public PageView(List<E> data,long elements,long pages) {
        super(data);
        this.elements = elements;
        this.pages = pages;
    }
}
