package nc.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nc.dao.ExchangeRateDao;
import nc.model.dto.ExchangeRateHistory;
import nc.model.dto.ExchangeRateInfo;
import nc.model.po.ExchangeRate;
import nc.model.po.ExchangeType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@Data
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "exchange.rate")
public class ExchangeRateService {
    private final ObjectMapper objectMapper;
    private final ExchangeRateDao exchangeRateDao;
    private final HttpClient httpClient;
    private String from;
    @Transactional(readOnly = true)
    public List<ExchangeRateInfo> info(ExchangeType from,List<ExchangeType> toList,LocalDate now){
        List<ExchangeRateInfo> infos = new ArrayList<>();
        toList = toList.stream().filter(exchangeType -> !exchangeType.equals(from)).toList();
        List<ExchangeRate> rates= exchangeRateDao.findAllByIdOutsetAndIdDestinationInAndIdCreateDate(from,toList,now)
                .stream()
                .filter(exchangeRate -> exchangeRate.getId().getDestination() != from)
                .sorted(Comparator.comparing(o -> o.getId().getDestination()))
                .toList();
        for (ExchangeRate exchangeRate : rates) {
            List<ExchangeRateHistory> histories = new ArrayList<>();
            ExchangeRateInfo info = new ExchangeRateInfo(
                    exchangeRate.getId().getDestination(),
                    exchangeRate.getRate(),
                    exchangeRate.getUpdateTime(),
                    histories);
            List<ExchangeRate.ExchangeRateId> ids = new ArrayList<>();
            for (int i =0;i<=30;i++){
                ids.add(new ExchangeRate.ExchangeRateId(from,exchangeRate.getId().getDestination(), now.plusDays(i*-1)));
            }
            histories.addAll(exchangeRateDao.findAllById(ids)
                    .stream()
                    .map(exchangeRate1 -> new ExchangeRateHistory(exchangeRate1.getId().getCreateDate(), exchangeRate1.getRate()))
                    .toList());
            infos.add(info);
        }
        return infos;
    }

    @Transactional
    @Scheduled(cron = "0 0 0/4 * * ?")
    public void initialize() throws IOException {
        List<String> types=Arrays.stream(ExchangeType.values()).map(ExchangeType::name).toList();
        for (ExchangeType value : ExchangeType.values()) {
                    Optional<ExchangeResult> optional = load(String.format(from,value.name()));
            if(optional.isPresent()){
                ExchangeResult result= optional.get();
                Map<String,String> conversionRate = result.getConversionRate();
                List<ExchangeRate> rates = new ArrayList<>();
                LocalDateTime now = LocalDateTime.now();
//                rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(value,ExchangeType.USD, LocalDate.now()),conversionRate.getUsd(), now));
                conversionRate.forEach((key, value1) -> {
                    if(types.contains(key)){
                        rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(value,ExchangeType.valueOf(key), LocalDate.now()),value1, now));
                    }
                });
                exchangeRateDao.saveAll(rates);
            }
        }
    }
    private Optional<ExchangeResult> load(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        HttpResponse response = httpClient.execute(get);
        try{
            HttpEntity entity = response.getEntity();
            StringBuilder json = new StringBuilder();
            json.append(EntityUtils.toString(entity));
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                return Optional.ofNullable(objectMapper.readValue(json.toString(),ExchangeResult.class));
            }else{
                log.warn("获取汇率失败:{}",json);

            }
        }finally {
            EntityUtils.consume(response.getEntity());
        }
        return Optional.empty();
    }

}
@Data
@JsonIgnoreProperties
class ExchangeResult{
    private String result;
    @JsonProperty("conversion_rates")
    private Map<String,String> conversionRate;
}
