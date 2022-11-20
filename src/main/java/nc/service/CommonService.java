package nc.service;

import lombok.AllArgsConstructor;
import nc.dao.CityDao;
import nc.model.dto.CityInfo;
import nc.model.dto.Currency;
import nc.model.dto.ServerInfo;
import nc.model.po.ExchangeType;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
public class CommonService implements ApplicationListener<ApplicationStartedEvent> {
    private final CityDao cityDao;
    private static final AtomicReference<ServerInfo> CACHE = new AtomicReference<>();
    public ServerInfo info(){
        return CACHE.get();
    }
    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0/1 * * * ?")
    public void initialize(){
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setCurrencies(Arrays.stream(ExchangeType.values()).map(exchangeType -> {
            Currency currency = new Currency();
            currency.setId(exchangeType.name());
            currency.setName(exchangeType.getDesc());
            return currency;
        }).toList());
        serverInfo.setCities(cityDao.findAll().stream().map(city -> {
            CityInfo cityInfo = new CityInfo();
            cityInfo.setId(city.getId());
            cityInfo.setName(city.getName());
            return cityInfo;
        }).toList());
        CACHE.set(serverInfo);
    }
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        initialize();
    }
}
