package com.tiza.process.common.protocol.general.cmd;

import com.tiza.process.common.support.bean.Header;
import com.tiza.process.common.support.bean.GeneralHeader;
import com.tiza.process.common.support.entity.Position;
import com.tiza.process.common.support.entity.Status;
import com.tiza.process.common.protocol.general.GeneralDataProcess;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.stereotype.Service;

/**
 * Description: CMD_8D
 * Author: DIYILIU
 * Update: 2017-08-03 19:15
 */

@Service
public class CMD_8D extends GeneralDataProcess {

    public CMD_8D() {
        this.cmd = 0x8D;
    }

    @Override
    public void parse(byte[] content, Header header) {
        GeneralHeader generalHeader = (GeneralHeader) header;

        ByteBuf buf = Unpooled.copiedBuffer(content);

        byte[] bytes = new byte[22];
        buf.readBytes(bytes);

        // 00H: 开机信息; 01H: 关机信息
        byte power = buf.readByte();

        Position position = renderPosition(bytes);
        Status status = renderStatus(position.getStatus());

        toKafka(generalHeader, position, status);
    }
}
