package nc.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Data
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "exchange.rate")
public class ExchangeRateService implements ApplicationListener<ApplicationReadyEvent> {
    private final ObjectMapper objectMapper;
    private final ExchangeRateDao exchangeRateDao;
    private final HttpClient httpClient;
    private String fromCNY;
    private String fromUSD;
    private String fromUAH;
    private String fromEUR;
    @Transactional(readOnly = true)
    public List<ExchangeRateInfo> info(ExchangeType from,LocalDate now){
        List<ExchangeRateInfo> infos = new ArrayList<>();
        List<ExchangeRate> rates= exchangeRateDao.findAllByIdOutsetAndIdCreateDate(from,now);
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
        initializeCNY();
        initializeUSD();
        initializeUAH();
        initializeEUR();
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
    private void initializeCNY() throws IOException {
        Optional<ExchangeResult> optional = load(fromCNY);
        if(optional.isPresent()){
            ExchangeResult result= optional.get();
            ConversionRate conversionRate = result.getConversionRate();
            List<ExchangeRate> rates = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(ExchangeType.CNY,ExchangeType.USD, LocalDate.now()),conversionRate.getUsd(), now));
            rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(ExchangeType.CNY,ExchangeType.UAH, LocalDate.now()),conversionRate.getUah(), now));
            rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(ExchangeType.CNY,ExchangeType.EUR, LocalDate.now()),conversionRate.getEur(), now));
            exchangeRateDao.saveAll(rates);
        }
    }
    private void initializeUAH() throws IOException {
        Optional<ExchangeResult> optional = load(fromUAH);
        if(optional.isPresent()){
            ExchangeResult result= optional.get();
            ConversionRate conversionRate = result.getConversionRate();
            List<ExchangeRate> rates = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(ExchangeType.UAH,ExchangeType.USD, LocalDate.now()),conversionRate.getUsd(), now));
            rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(ExchangeType.UAH,ExchangeType.CNY, LocalDate.now()),conversionRate.getCny(), now));
            rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(ExchangeType.UAH,ExchangeType.EUR, LocalDate.now()),conversionRate.getEur(), now));
            exchangeRateDao.saveAll(rates);
        }
    }
    private void initializeUSD() throws IOException {
        Optional<ExchangeResult> optional = load(fromUSD);
        if(optional.isPresent()){
            ExchangeResult result= optional.get();
            ConversionRate conversionRate = result.getConversionRate();
            List<ExchangeRate> rates = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(ExchangeType.USD,ExchangeType.CNY, LocalDate.now()),conversionRate.getCny(), now));
            rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(ExchangeType.USD,ExchangeType.UAH, LocalDate.now()),conversionRate.getUah(), now));
            rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(ExchangeType.USD,ExchangeType.EUR, LocalDate.now()),conversionRate.getEur(), now));
            exchangeRateDao.saveAll(rates);
        }
    }
    private void initializeEUR() throws IOException {
        Optional<ExchangeResult> optional = load(fromEUR);
        if(optional.isPresent()){
            ExchangeResult result= optional.get();
            ConversionRate conversionRate = result.getConversionRate();
            List<ExchangeRate> rates = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(ExchangeType.EUR,ExchangeType.CNY, LocalDate.now()),conversionRate.getCny(), now));
            rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(ExchangeType.EUR,ExchangeType.UAH, LocalDate.now()),conversionRate.getUah(), now));
            rates.add(new ExchangeRate(new ExchangeRate.ExchangeRateId(ExchangeType.EUR,ExchangeType.USD, LocalDate.now()),conversionRate.getUsd(), now));
            exchangeRateDao.saveAll(rates);
        }
    }

    @Override
    @SneakyThrows
    public void onApplicationEvent(ApplicationReadyEvent event) {
       // initialize();
    }
}
@Data
@JsonIgnoreProperties
class ExchangeResult{
    private String result;
    @JsonProperty("conversion_rates")
    private ConversionRate conversionRate;
}
@Data
@JsonIgnoreProperties
class ConversionRate{
    @JsonProperty("USD")
    private String usd;
    @JsonProperty("UAH")
    private String uah;
    @JsonProperty("EUR")
    private String eur;
    @JsonProperty("CNY")
    private String cny;
}
