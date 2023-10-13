package com.example.edvhelper.mapper;

import com.example.edvhelper.model.AppUser;
import com.example.edvhelper.model.FiscalId;
import com.example.edvhelper.model.FiscalIdStatusEnum;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class FiscalIdMapper {

    public FiscalId creatNewFiscalId(AppUser appUser, String fiscalId) {
        return FiscalId
                .builder()
                .appUser(appUser)
                .fiscalId(fiscalId)
                .status(FiscalIdStatusEnum.NEW)
                .build();
    }
}
