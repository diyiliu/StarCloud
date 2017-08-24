package com.tiza.process.cstar_tiza.protocol;

import cn.com.tiza.tstar.common.process.RPTuple;
import com.tiza.process.common.protocol.general.GeneralDataProcess;
import com.tiza.process.common.support.bean.GeneralHeader;
import com.tiza.process.common.support.config.Constant;
import com.tiza.process.common.support.entity.Parameter;
import com.tiza.process.common.support.entity.Position;
import com.tiza.process.common.support.entity.Status;
import com.tiza.process.common.support.entity.VehicleInfo;
import com.tiza.process.common.util.DateUtil;
import com.tiza.process.common.util.JacksonUtil;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: CStarDataProcess
 * Author: DIYILIU
 * Update: 2017-08-24 10:35
 */
public class CStarDataProcess extends GeneralDataProcess {

    @Override
    public void toKafka(GeneralHeader header, Position position, Status status, Parameter parameter) {
        String terminalId = header.getTerminalId();
        if (!vehicleCacheProvider.containsKey(terminalId)) {
            logger.warn("该终端[{}]不存在车辆列表中...", terminalId);
            return;
        }

        VehicleInfo vehicle = (VehicleInfo) vehicleCacheProvider.get(terminalId);

        Map posMap = new HashMap();
        posMap.put(Constant.Location.GPS_TIME,
                DateUtil.dateToString(position.getDateTime()));
        posMap.put(Constant.Location.SPEED, position.getSpeed());
        posMap.put(Constant.Location.ALTITUDE, position.getHeight());
        posMap.put(Constant.Location.DIRECTION, position.getDirection());
        posMap.put(Constant.Location.LOCATION_STATUS, status.getLocation());
        posMap.put(Constant.Location.ACC_STATUS, status.getAcc());
        posMap.put(Constant.Location.ORIGINAL_LNG, position.getLngD());
        posMap.put(Constant.Location.ORIGINAL_LAT, position.getLatD());
        posMap.put(Constant.Location.LNG, position.getEnLngD());
        posMap.put(Constant.Location.LAT, position.getLatD());

        // 正反转、转速、油位
        posMap.put(Constant.Location.ROTATE_DIRECTION, parameter.getRotateDirection());
        posMap.put(Constant.Location.ROTATE_SPEED, parameter.getRotateSpeed());
        posMap.put(Constant.Location.FUEL_VOLUME, parameter.getFuelVolume());

        posMap.put("VehicleId", vehicle.getId());

        RPTuple rpTuple = new RPTuple();
        rpTuple.setCmdID(header.getCmd());
        rpTuple.setCmdSerialNo(header.getSerial());

        rpTuple.setTerminalID(String.valueOf(vehicle.getId()));

        String msgBody = JacksonUtil.toJson(posMap);
        rpTuple.setMsgBody(msgBody.getBytes(Charset.forName("UTF-8")));
        rpTuple.setTime(position.getDateTime().getTime());

        // 将解析的位置和状态信息放入流中
        RPTuple tuple = (RPTuple) header.gettStarData();
        tuple.setTerminalID(String.valueOf(vehicle.getId()));

        Map<String, String> context = tuple.getContext();
        context.put(Constant.FlowKey.POSITION, JacksonUtil.toJson(position));
        context.put(Constant.FlowKey.STATUS, JacksonUtil.toJson(status));
        context.put(Constant.FlowKey.PARAMETER, JacksonUtil.toJson(parameter));

        logger.info("终端[{}]写入Kafka位置信息...", terminalId);
        handler.storeInKafka(rpTuple, context.get(Constant.Kafka.TRACK_TOPIC));
    }
}
