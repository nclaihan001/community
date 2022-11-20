package nc.endpoint;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import nc.model.dto.ExchangeRateInfo;
import nc.model.dto.PhoneInfo;
import nc.model.dto.ServerInfo;
import nc.model.po.ExchangeType;
import nc.model.view.ListView;
import nc.model.view.ObjectView;
import nc.service.CommonService;
import nc.service.ExchangeRateService;
import nc.service.PhoneService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@RestController
@Tag(name = "公共接口")
@AllArgsConstructor
@RequestMapping(value = "/",produces = MediaType.APPLICATION_JSON_VALUE)
public class CommonEndPoint {
    private static final Cache<ExchangeRateCacheId, List<ExchangeRateInfo>> CACHE = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();
    private final ExchangeRateService exchangeRateService;
    private final PhoneService phoneService;
    private final CommonService commonService;
    @Operation(summary = "获取汇率信息")
    @GetMapping(value = "exchange_rate")
    public ListView<ExchangeRateInfo> findAll(
            @Parameter(description = "源币种")
            @RequestParam ExchangeType from,
            @Parameter(description = "源币种")
            @RequestParam List<ExchangeType> toList){
        return new ListView<>(CACHE.get(new ExchangeRateCacheId(from,toList, LocalDate.now()),
                exchangeRateId -> exchangeRateService.info(from,toList, LocalDate.now())));
    }
    @Operation(summary = "获取常用电话列表")
    @GetMapping(value = "phone_list")
    public ListView<PhoneInfo> findAll(){
        return new ListView<>(phoneService.findAll());
    }
    @Operation(summary = "获取服务器信息")
    @GetMapping(value = "server_info")
    public ObjectView<ServerInfo> serverInfo(){
        return new ObjectView<>(commonService.info());
    }
}
@Data
@AllArgsConstructor
class ExchangeRateCacheId{
    private ExchangeType from;
    private List<ExchangeType> toList;
    private LocalDate createDate;
}
