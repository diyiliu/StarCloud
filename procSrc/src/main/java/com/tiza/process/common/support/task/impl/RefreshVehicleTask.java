package com.tiza.process.common.support.task.impl;

import com.tiza.process.common.support.cache.ICache;
import com.tiza.process.common.support.entity.VehicleInfo;
import com.tiza.process.common.support.dao.VehicleDao;
import com.tiza.process.common.support.task.ITask;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: RefreshVehicleTask
 * Author: DIYILIU
 * Update: 2017-08-07 16:09
 */
public class RefreshVehicleTask implements ITask {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ICache vehicleCacheProvider;

    @Resource
    private VehicleDao vehicleDao;

    @Override
    public void execute() {
        logger.info("刷新车辆列表...");

        List<VehicleInfo> vehicleInfos = vehicleDao.selectVehicleInfo();
        refresh(vehicleInfos, vehicleCacheProvider);
    }


    private void refresh(List<VehicleInfo> vehicleInfos, ICache vehicleCache) {
        if (vehicleInfos == null || vehicleInfos.size() < 1){
            logger.warn("无车辆信息！");
            return;
        }

        Set oldKeys = vehicleCache.getKeys();
        Set tempKeys = new HashSet<>(vehicleInfos.size());

        for (VehicleInfo vehicle : vehicleInfos) {
            vehicleCache.put(vehicle.getSim(), vehicle);
            tempKeys.add(vehicle.getSim());
        }

        Collection subKeys = CollectionUtils.subtract(oldKeys, tempKeys);
        for (Iterator iterator = subKeys.iterator(); iterator.hasNext();){
            String key = (String) iterator.next();
            vehicleCache.remove(key);
        }
    }

}
