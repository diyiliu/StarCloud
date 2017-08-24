package com.tiza.process.common.protocol.general.cmd;

import com.tiza.process.common.support.bean.Header;
import com.tiza.process.common.protocol.general.GeneralDataProcess;
import org.springframework.stereotype.Service;

/**
 * Description: CMD_86
 * Author: DIYILIU
 * Update: 2017-08-03 19:15
 */

@Service
public class CMD_86 extends GeneralDataProcess {

    public CMD_86() {
        this.cmd = 0x86;
    }

    @Override
    public void parse(byte[] content, Header header) {



    }
}
