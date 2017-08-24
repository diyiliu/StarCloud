package com.tiza.gateways.mstar_tiza;

import com.tiza.gateways.common.GeneralHandler;

/**
 * Description: MStarHandler
 * Author: DIYILIU
 * Update: 2017-08-23 10:01
 */
public class MStarHandler extends GeneralHandler{

    /**
     * 构造方法，加载配置文件。
     */
    public MStarHandler() {
        loadResources("mstar_tiza.properties");
    }
}
