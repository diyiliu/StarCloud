package com.tiza.gateways.cstar_tiza;

import com.tiza.gateways.common.GeneralHandler;

/**
 * Description: CStarHandler
 * Author: DIYILIU
 * Update: 2017-08-23 10:01
 */
public class CStarHandler extends GeneralHandler{

    /**
     * 构造方法，加载配置文件。
     */
    public CStarHandler() {
        loadResources("cstar_tiza.properties");
    }
}
