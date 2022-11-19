package nc.model.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 常用电话
 */
@Getter
@Setter
@Entity
@Table(name = "t_phone")
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name",columnDefinition = "TEXT")
    private String name;
    @Column(name = "phone",length = 15)
    private String phone;
}
