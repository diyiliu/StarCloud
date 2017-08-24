package com.tiza.process.cstar_tiza;

import cn.com.tiza.tstar.common.process.RPTuple;
import com.tiza.process.common.handler.GeneralParseHandler;
import com.tiza.process.common.support.bean.GeneralHeader;
import com.tiza.process.common.support.cache.ICache;
import com.tiza.process.common.support.config.Constant;
import com.tiza.process.common.util.CommonUtil;
import com.tiza.process.common.util.SpringUtil;
import com.tiza.process.cstar_tiza.protocol.CStarDataProcess;

/**
 * Description: MStarParseHandler
 * Author: DIYILIU
 * Update: 2017-08-23 14:51
 */
public class CStarParseHandler extends GeneralParseHandler {

    @Override
    public RPTuple handle(RPTuple rpTuple) throws Exception {
        logger.info("收到终端[{}], 指令[{}]...", rpTuple.getTerminalID(), CommonUtil.toHex(rpTuple.getCmdID()));

        ICache cmdCacheProvider = SpringUtil.getBean("cmdCacheProvider");
        CStarDataProcess process = (CStarDataProcess) cmdCacheProvider.get(rpTuple.getCmdID());
        if (process == null) {
            logger.error("无法找到[{}]指令解析器!", CommonUtil.toHex(rpTuple.getCmdID()));
            return null;
        }

        rpTuple.getContext().put(Constant.Kafka.TRACK_TOPIC, processorConf.get("trackTopic"));
        GeneralHeader header = (GeneralHeader) process.dealHeader(rpTuple.getMsgBody());
        header.settStarData(rpTuple);
        process.parse(header.getContent(), header);

        return rpTuple;
    }
}
