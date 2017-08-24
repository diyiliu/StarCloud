package com.tiza.process.common.protocol.general.cmd;

import com.tiza.process.common.support.bean.Header;
import com.tiza.process.common.support.bean.GeneralHeader;
import com.tiza.process.common.support.entity.Position;
import com.tiza.process.common.support.entity.Status;
import com.tiza.process.common.protocol.general.GeneralDataProcess;
import org.springframework.stereotype.Service;

/**
 * Description: CMD_85
 * Author: DIYILIU
 * Update: 2017-08-03 19:15
 */

@Service
public class CMD_85 extends GeneralDataProcess {

    public CMD_85() {
        this.cmd = 0x85;
    }

    @Override
    public void parse(byte[] content, Header header) {
        GeneralHeader generalHeader = (GeneralHeader) header;

        Position position = renderPosition(content);
        Status status = renderStatus(position.getStatus());

        toKafka(generalHeader, position, status);
    }
}
