package com.tiza.process.cstar_tiza.module;

import cn.com.tiza.tstar.common.process.RPTuple;
import com.tiza.process.common.handler.module.CurrentStatusModule;
import com.tiza.process.common.support.config.Constant;
import com.tiza.process.common.support.dao.VehicleDao;
import com.tiza.process.common.support.entity.Parameter;
import com.tiza.process.common.support.entity.Position;
import com.tiza.process.common.support.entity.Status;
import com.tiza.process.common.util.JacksonUtil;
import com.tiza.process.common.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Description: CStarCurrentStatus
 * Author: DIYILIU
 * Update: 2017-08-24 10:44
 */
public class CStarCurrentStatus extends CurrentStatusModule {

    @Override
    public RPTuple handle(RPTuple rpTuple) throws Exception {
        Map<String, String> context = rpTuple.getContext();

        String vehicleId = rpTuple.getTerminalID();
        if (context.containsKey(Constant.FlowKey.POSITION) && context.containsKey(Constant.FlowKey.STATUS)) {
            Position position = JacksonUtil.toObject(context.get(Constant.FlowKey.POSITION), Position.class);
            Status status = JacksonUtil.toObject(context.get(Constant.FlowKey.STATUS), Status.class);

            VehicleDao vehicleDao = SpringUtil.getBean("vehicleDao");
            Object[] values;

            // 更新当前位置表，包括工作参数(正反转，转速，油量)
            if (context.containsKey(Constant.FlowKey.PARAMETER)) {
                Parameter parameter = JacksonUtil.toObject(context.get(Constant.FlowKey.PARAMETER), Parameter.class);

                values = new Object[]{position.getEnLngD(), position.getLatD(),
                        position.getSpeed(), position.getDirection(), position.getHeight(), position.getDateTime(),
                        status.getAcc(), status.getLocation(), status.getPowerOff(),
                        status.getLowPower(), status.getGpsFault(), status.getLoseAntenna(),
                        parameter.getRotateDirection(), parameter.getRotateSpeed(), parameter.getFuelVolume(), vehicleId};

                if (vehicleDao.update(Constant.getSQL(Constant.SQL.UPDATE_VEHICLE_WORK_PARAMETER), values)) {
                    logger.info("车辆[{}]更新当前表车辆位置和工况信息...", vehicleId);
                } else {
                    logger.warn("车辆[{}]更新当前表车辆位置和工况信息失败!", vehicleId);
                }

                return rpTuple;
            }

            values = new Object[]{position.getEnLngD(), position.getLatD(),
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
}
