package com.tiza.process.common.protocol.general.cmd;

import com.tiza.process.common.support.bean.Header;
import com.tiza.process.common.protocol.general.GeneralDataProcess;
import org.springframework.stereotype.Service;

/**
 * Description: CMD_84
 * Author: DIYILIU
 * Update: 2017-08-08 16:51
 */

@Service
public class CMD_84 extends GeneralDataProcess {

    public CMD_84() {
        this.cmd = 0x84;
    }

    @Override
    public void parse(byte[] content, Header header) {

    }
}
