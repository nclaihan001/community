package nc.dao;

import nc.model.po.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneDao extends JpaRepository<Phone,Integer> {
}
