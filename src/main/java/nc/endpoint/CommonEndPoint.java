package nc.endpoint;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import nc.model.dto.ExchangeRateInfo;
import nc.model.dto.PhoneInfo;
import nc.model.po.ExchangeRate;
import nc.model.po.ExchangeType;
import nc.model.view.ListView;
import nc.service.ExchangeRateService;
import nc.service.PhoneService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "公共接口")
@AllArgsConstructor
@RequestMapping(value = "/",produces = MediaType.APPLICATION_JSON_VALUE)
public class CommonEndPoint {
    private static final Cache<ExchangeRate.ExchangeRateId, List<ExchangeRateInfo>> CACHE = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofMinutes(30))
            .build();
    private final ExchangeRateService exchangeRateService;
    private final PhoneService phoneService;
    @Operation(summary = "获取汇率信息")
    @GetMapping(value = "exchange_rate/{from}")
    public ListView<ExchangeRateInfo> findAll(
            @Parameter(description = "源币种")
            @PathVariable ExchangeType from){
        return new ListView<>(CACHE.get(new ExchangeRate.ExchangeRateId(from,null, LocalDate.now()),
                exchangeRateId -> exchangeRateService.info(from, LocalDate.now())));
    }
    @Operation(summary = "获取常用电话列表")
    @GetMapping(value = "phone_list")
    public ListView<PhoneInfo> findAll(){
        return new ListView<>(phoneService.findAll());
    }
}
