package com.tiza.process.common.protocol.general.cmd;

import com.tiza.process.common.support.bean.Header;
import com.tiza.process.common.protocol.general.GeneralDataProcess;
import org.springframework.stereotype.Service;

/**
 * Description: CMD_8B
 * Author: DIYILIU
 * Update: 2017-08-03 19:15
 */

@Service
public class CMD_8B extends GeneralDataProcess {

    public CMD_8B() {
        this.cmd = 0x8B;
    }

    @Override
    public void parse(byte[] content, Header header) {
        super.parse(content, header);
    }
}
