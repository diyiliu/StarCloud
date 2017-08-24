package com.tiza.process.common.handler;

import cn.com.tiza.tstar.common.datasource.BusinessDBManager;
import cn.com.tiza.tstar.common.process.BaseHandle;
import cn.com.tiza.tstar.common.process.RPTuple;
import cn.com.tiza.tstar.common.utils.DBUtil;
import com.tiza.process.common.support.config.Constant;
import com.tiza.process.common.protocol.general.GeneralDataProcess;
import com.tiza.process.common.support.cache.ICache;
import com.tiza.process.common.util.CommonUtil;
import com.tiza.process.common.util.SpringUtil;
import com.tiza.process.common.support.bean.GeneralHeader;
import com.tiza.process.common.support.dao.base.BaseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Description: GeneralParseHandler
 * Author: DIYILIU
 * Update: 2017-08-02 14:06
 */

public class GeneralParseHandler extends BaseHandle {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public RPTuple handle(RPTuple rpTuple) throws Exception {
        logger.info("收到终端[{}], 指令[{}]...", rpTuple.getTerminalID(), CommonUtil.toHex(rpTuple.getCmdID()));

        ICache cmdCacheProvider = SpringUtil.getBean("cmdCacheProvider");
        GeneralDataProcess process = (GeneralDataProcess) cmdCacheProvider.get(rpTuple.getCmdID());
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

    @Override
    public void init() throws Exception {
        GeneralDataProcess.setHandle(this);
        SpringUtil.init();

        BusinessDBManager dbManager = BusinessDBManager.getInstance(this.processorConf);
        Field field = dbManager.getClass().getDeclaredField("dbUtil");
        field.setAccessible(true);
        DBUtil dbUtil = (DBUtil) field.get(dbManager);
        field.setAccessible(false);

        // 初始化数据源
        BaseDao.initDataSource(dbUtil.getDataSource());

        // 初始化SQL
        Constant.init(this.processorConf.get("initSql"));
    }
}
