package com.tiza.process.common.protocol.general.cmd;

import com.tiza.process.common.support.bean.Header;
import com.tiza.process.common.protocol.general.GeneralDataProcess;
import com.tiza.process.common.support.bean.GeneralHeader;
import com.tiza.process.common.support.entity.Position;
import com.tiza.process.common.support.entity.Status;
import org.springframework.stereotype.Service;

/**
 * Description: CMD_83
 * Author: DIYILIU
 * Update: 2017-08-03 19:15
 */

@Service
public class CMD_83 extends GeneralDataProcess {

    public CMD_83() {
        this.cmd = 0x83;
    }

    @Override
    public void parse(byte[] content, Header header) {
        GeneralHeader generalHeader = (GeneralHeader) header;

        Position position = renderPosition(content);
        Status status = renderStatus(position.getStatus());

        toKafka(generalHeader, position, status);
    }
}
