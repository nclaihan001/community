package nc.model.po;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 汇率实体
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_exchange_rate")
public class ExchangeRate implements Serializable {
    @EmbeddedId
    private ExchangeRateId id;
    @Column(length = 50)
    private String rate;
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class ExchangeRateId implements Serializable{
        @Column(columnDefinition = "SMALLINT")
        @Enumerated(EnumType.ORDINAL)
        private ExchangeType outset;
        @Column(columnDefinition = "SMALLINT")
        @Enumerated(EnumType.ORDINAL)
        private ExchangeType destination;
        @Column(name = "create_date")
        private LocalDate createDate;
    }
}
