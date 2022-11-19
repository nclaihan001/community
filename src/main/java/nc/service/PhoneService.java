package nc.service;

import lombok.AllArgsConstructor;
import nc.dao.PhoneDao;
import nc.model.dto.PhoneInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PhoneService {
    private final PhoneDao phoneDao;
    @Transactional(readOnly = true)
    public List<PhoneInfo> findAll(){
        return phoneDao.findAll().stream().map(phone -> {
            PhoneInfo info = new PhoneInfo();
            info.setPhone(phone.getPhone());
            info.setName(phone.getName());
            return info;
        }).toList();
    }
}
