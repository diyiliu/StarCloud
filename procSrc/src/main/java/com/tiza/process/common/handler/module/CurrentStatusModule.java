package com.tiza.process.common.handler.module;

import cn.com.tiza.tstar.common.process.BaseHandle;
import cn.com.tiza.tstar.common.process.RPTuple;
import com.tiza.process.common.support.config.Constant;
import com.tiza.process.common.support.dao.VehicleDao;
import com.tiza.process.common.support.entity.Position;
import com.tiza.process.common.support.entity.Status;
import com.tiza.process.common.util.JacksonUtil;
import com.tiza.process.common.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Description: CurrentStatusModule
 * Author: DIYILIU
 * Update: 2017-08-09 10:47
 */

public class CurrentStatusModule extends BaseHandle {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public RPTuple handle(RPTuple rpTuple) throws Exception {
        Map<String, String> context = rpTuple.getContext();

        String vehicleId = rpTuple.getTerminalID();
        if (context.containsKey(Constant.FlowKey.POSITION) && context.containsKey(Constant.FlowKey.STATUS)) {
            Position position = JacksonUtil.toObject(context.get(Constant.FlowKey.POSITION), Position.class);
            Status status = JacksonUtil.toObject(context.get(Constant.FlowKey.STATUS), Status.class);

            VehicleDao vehicleDao = SpringUtil.getBean("vehicleDao");
            Object[] values = new Object[]{position.getEnLngD(), position.getLatD(),
                    position.getSpeed(), position.getDirection(), position.getHeight(), position.getDateTime(),
                    status.getAcc(), status.getLocation(), status.getPowerOff(), status.getLowPower(),
                    status.getGpsFault(), status.getLoseAntenna(), vehicleId};
            if (vehicleDao.update(Constant.getSQL(Constant.SQL.UPDATE_VEHICLE_GPS_INFO), values)) {
                logger.info("车辆[{}]更新当前表位置信息...", vehicleId);
            } else {
                logger.warn("车辆[{}]更新当前表位置信息失败!", vehicleId);
            }
        }

        return rpTuple;
    }

    @Override
    public void init() throws Exception {

    }
}
