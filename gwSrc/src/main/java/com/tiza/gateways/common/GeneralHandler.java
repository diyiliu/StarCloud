package com.tiza.gateways.common;

import cn.com.tiza.tstar.common.entity.TStarData;
import cn.com.tiza.tstar.gateway.entity.AckData;
import cn.com.tiza.tstar.gateway.entity.CommandData;
import cn.com.tiza.tstar.gateway.handler.BaseUserDefinedHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Description: GeneralHandler
 * Author: DIYILIU
 * Update: 2017-08-23 10:00
 */

public class GeneralHandler extends BaseUserDefinedHandler {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private Properties properties = new Properties();

    protected void loadResources(String configPath) {
        InputStream in = null;
        try {
            in = ClassLoader.getSystemResourceAsStream(configPath);
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public TStarData handleRecvMessage(ChannelHandlerContext context, ByteBuf byteBuf) {
        TStarData tStarData = new TStarData();

        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);

        tStarData.setMsgBody(bytes);
        ByteBuf buf = Unpooled.copiedBuffer(bytes);
        buf.readShort();
        byte[] terminalArray = new byte[5];
        buf.readBytes(terminalArray);

        String terminal = CommonUtil.parseTerminal(terminalArray);
        tStarData.setTerminalID(terminal);

        buf.readInt();
        int serial = buf.readUnsignedShort();
        int cmd = buf.readUnsignedByte();

        tStarData.setCmdID(cmd);
        tStarData.setCmdSerialNo(serial);
        tStarData.setTime(System.currentTimeMillis());

        logger.info("收到消息，终端[{}]指令[{}], 内容[{}]...", terminal, String.format("%02X", cmd), CommonUtil.bytesToStr(bytes));

        TStarData respData = new TStarData();
        respData.setTerminalID(terminal);
        respData.setTime(System.currentTimeMillis());

        ByteBuf respBuf;
        byte[] respMsg;
        switch (cmd) {

            // 请求中心地址
            case 0x80:
                String apn = properties.getProperty("apn");
                String ip = properties.getProperty("ip");
                int port = Integer.parseInt(properties.getProperty("port"));

                byte[] apnBytes = apn.getBytes();

                respBuf = Unpooled.buffer(1 + apnBytes.length + 4 + 2);
                respBuf.writeByte(apnBytes.length);
                respBuf.writeBytes(apnBytes);
                respBuf.writeBytes(CommonUtil.ipToBytes(ip));
                respBuf.writeShort(port);

                respMsg = createResp(bytes, respBuf.array(), 0x01);
                respData.setCmdID(0x01);
                respData.setMsgBody(respMsg);
                context.channel().writeAndFlush(respData);
                break;

            // 登录指令
            case 0x85:
                respBuf = Unpooled.buffer(4);
                respBuf.writeShort(serial);
                respBuf.writeByte(cmd);
                respBuf.writeByte(0);

                respMsg = createResp(bytes, respBuf.array(), 0x02);
                respData.setCmdID(0x02);
                respData.setMsgBody(respMsg);
                context.channel().writeAndFlush(respData);
                break;
            default:
                break;
        }

        return tStarData;
    }

    @Override
    public AckData ackHandle(ChannelHandlerContext ctx, TStarData msg) {
        AckData ackData = null;
        int cmd = msg.getCmdID();

        ByteBuf buf;
        String terminalId = msg.getTerminalID();

        byte[] bytes;
        int respCmd = 0xFF;
        int serial = 0;
        switch (cmd) {

            // 终端回复
            case 0x82:
                bytes = msg.getMsgBody();
                buf = Unpooled.copiedBuffer(bytes, 14, bytes.length - 14);

                serial = buf.readShort();
                respCmd = buf.readByte();
                ackData = new AckData(msg, respCmd, serial);
                break;

            // 定位指令(上传的指令不包含下发序列号，自己组装回复序列号)
            case 0x83:
                respCmd = 0x03;

                // sim卡后四位 + respCmd
                serial = Integer.parseInt(terminalId.substring(terminalId.length() - 4)) + respCmd;
                ackData = new AckData(msg, respCmd, serial);
                break;

            // 终端参数(上传的指令不包含下发序列号，自己组装回复序列号)
            case 0x84:
                bytes = msg.getMsgBody();
                buf = Unpooled.copiedBuffer(bytes, 14, bytes.length - 14);
                buf.readByte();
                int paramId = buf.readShort();

                respCmd = 0x07;
                // sim卡后四位 + respCmd + 参数id
                serial = Integer.parseInt(terminalId.substring(terminalId.length() - 4)) + respCmd + paramId;
                ackData = new AckData(msg, respCmd, serial);
                break;
            default:
                break;
        }

        if (ackData != null){
            logger.info("响应指令[{}, {}], 流水号[{}]", CommonUtil.toHex(cmd), CommonUtil.toHex(respCmd), serial);
        }

        return ackData;
    }


    @Override
    public void commandReceived(ChannelHandlerContext ctx, CommandData cmd) {

        logger.info("下发消息，终端[{}]指令[{}], 内容[{}]...", cmd.getTerminalID(), String.format("%02X", cmd), CommonUtil.bytesToStr(cmd.getMsgBody()));
    }

    /**
     * 命令序号
     **/
    private static AtomicLong msgSerial = new AtomicLong(0);

    private int getMsgSerial() {
        Long serial = msgSerial.incrementAndGet();
        if (serial > 65535) {
            msgSerial.set(0);
            serial = msgSerial.incrementAndGet();
        }

        return serial.intValue();
    }

    /**
     * 生成回复指令内容
     *
     * @param recMsg  收到上行的指令内容
     * @param content 需要下发的指令内容
     * @param cmd     需要下发的命令ID
     * @return
     */
    public byte[] createResp(byte[] recMsg, byte[] content, int cmd) {

        int length = 14 + content.length;
        recMsg[0] = (byte) ((length >> 8) & 0xff);
        recMsg[1] = (byte) (length & 0xff);

        ByteBuf header = Unpooled.copiedBuffer(recMsg, 0, 11);

        ByteBuf remainder = Unpooled.buffer(3 + content.length + 3);
        remainder.writeShort(getMsgSerial());
        remainder.writeByte(cmd);
        remainder.writeBytes(content);

        byte check = CommonUtil.checkBits(Unpooled.copiedBuffer(header.array(),
                Unpooled.copiedBuffer(remainder.array(), 0, 3 + content.length).array()).array());

        remainder.writeByte(check);
        remainder.writeByte(0x0D);
        remainder.writeByte(0x0A);

        return Unpooled.copiedBuffer(header, remainder).array();
    }
}
