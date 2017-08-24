package com.tiza.process.common.protocol.general.cmd;

import com.tiza.process.common.support.bean.Header;
import com.tiza.process.common.protocol.general.GeneralDataProcess;
import org.springframework.stereotype.Service;

/**
 * Description: CMD_80
 * Author: DIYILIU
 * Update: 2017-08-03 19:15
 */

@Service
public class CMD_80 extends GeneralDataProcess {

    public CMD_80() {
        this.cmd = 0x80;
    }

    @Override
    public void parse(byte[] content, Header header) {


    }
}
