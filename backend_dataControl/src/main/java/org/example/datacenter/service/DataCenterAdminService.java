package org.example.datacenter.service;

import org.example.datacenter.mapper.DataCenterAdminMapper;
import org.example.datacenter.model.DataCenterAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataCenterAdminService {
    @Autowired
    private DataCenterAdminMapper dataCenterAdminMapper;
    public boolean checkUserExistence(DataCenterAdmin datacenteradmin) {
        return dataCenterAdminMapper.checkUserExistence(datacenteradmin.getName()) > 0;
    }

    public DataCenterAdmin getById(Integer integer) {
        return dataCenterAdminMapper.getById(integer);
    }
}