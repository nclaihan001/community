package nc.dao;

import nc.model.po.ExchangeRate;
import nc.model.po.ExchangeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExchangeRateDao extends JpaRepository<ExchangeRate, ExchangeRate.ExchangeRateId> {
    List<ExchangeRate> findAllByIdOutsetAndIdCreateDate(ExchangeType outset, LocalDate createDate);
}
