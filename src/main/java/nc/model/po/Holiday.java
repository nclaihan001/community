package nc.model.po;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 假日信息
 */
@Getter
@Setter
@Entity
@Table(name = "t_holiday",indexes = {
        @Index(name = "holiday_month_idx",columnList = "month")
})
public class Holiday {
    @EmbeddedId
    private HolidayId id;
    @Column(length = 7)
    private String month;
    @Column
    private boolean reset;
    @Column(name = "description",length = 200)
    private String desc;
    @Data
    @Embeddable
    public static class HolidayId implements Serializable {
        @Column(length = 20)
        private String name;
        @Column
        private LocalDate date;
    }
}
