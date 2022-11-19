package nc.model.po;

import lombok.Getter;

@Getter
public enum ExchangeType {
    USD("美元"),UAH("格里夫纳"),EUR("欧元"),CNY("人民币");
    private final String desc;

    ExchangeType(String desc) {
        this.desc = desc;
    }
}
