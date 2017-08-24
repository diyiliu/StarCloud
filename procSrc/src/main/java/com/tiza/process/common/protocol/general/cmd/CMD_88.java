package com.tiza.process.common.protocol.general.cmd;

import com.tiza.process.common.support.bean.Header;
import com.tiza.process.common.support.bean.GeneralHeader;
import com.tiza.process.common.protocol.general.GeneralDataProcess;
import com.tiza.process.common.util.CommonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description: CMD_88
 * Author: DIYILIU
 * Update: 2017-08-03 19:15
 */

@Service
public class CMD_88 extends GeneralDataProcess {

    public CMD_88() {
        this.cmd = 0x88;
    }

    @Override
    public void parse(byte[] content, Header header) {
        GeneralHeader generalHeader = (GeneralHeader) header;

        ByteBuf buf = Unpooled.copiedBuffer(content);

        // 年月日
        byte[] ymd = new byte[3];
        buf.readBytes(ymd);

        List<Date> dateList = new ArrayList<>();
        int count = buf.readByte();
        // 时分秒
        byte[] hms = new byte[3];
        for (int i = 0; i < count; i++){
            buf.readBytes(hms);

            // 拼接 年月日 + 时分秒 = 完整日期
            ByteBuf dateBuf = Unpooled.copiedBuffer(ymd, hms);
            Date date = CommonUtil.bytesToDate(dateBuf.array());
            dateList.add(date);
        }
    }
}
