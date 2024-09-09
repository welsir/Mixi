package com.mixi.server.netty.codec;

import com.mixi.server.common.compress.VarInt;
import com.mixi.server.netty.protocol.AccessMessage;
import com.mixi.server.netty.protocol.Header;
import com.mixi.server.util.BytesUtils;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description
 * @Author welsir
 * @Date 2024/6/24 10:22
 */
public class MessageCodec {

    private static Logger log = LoggerFactory.getLogger(MessageCodec.class);
    private static final int MAX_BODY_SIZE_TO_COMPRESS = 2048;

    public static void encode(ByteBuf buf, AccessMessage msg) {
        buf.writeByte(msg.getVersion());
        buf.writeBoolean(msg.isHeartBeat());
        if (msg.isHeartBeat()) {
            return;
        }
        buf.writeByte(msg.getCmd());

        List<Header> headers = msg.getHeaders();
        int headerCount = headers == null ? 0 : headers.size();
        int headerLength = 0;
        for (int i = 0; i < headerCount; i++) {
            headerLength += headers.get(i).calculateTotalLength();
        }
        byte[] body = msg.getBody();
        int bodyLength = body == null ? 0 : body.length;
        msg.setLength(headerLength + bodyLength);
        VarInt.writeVarInt32(buf, msg.getLength());

        buf.writeByte(headerCount);
        for (int i = 0; i < headerCount; i++) {
            Header header = headers.get(i);
            VarInt.writeVarInt32(buf, header.calculateDataLength());
            buf.writeByte(header.getType());
            buf.writeBytes(header.getData());
        }

        if (bodyLength != 0) {
            buf.writeBytes(body);
        }

    }

    public static AccessMessage decode(ByteBuf buf) {
        AccessMessage msg = new AccessMessage();
        try {
            msg.setVersion(buf.readByte());
            msg.setHeartBeat(buf.readBoolean());
            if(msg.isHeartBeat()){
                return msg;
            }
            msg.setCmd(buf.readByte());
            msg.setLength(VarInt.readVarInt32(buf));
            int totalLength = msg.getLength();
            List<Header> headers = new ArrayList<>();
            int headerCount = buf.readByte();
            for (int i = 0; i < headerCount; i++) {
                int headerLength = VarInt.readVarInt32(buf);
                int headerType = buf.readByte();
                Header header = new Header(headerType, BytesUtils.getFromBuf(buf, headerLength));
                headers.add(header);
                totalLength-=header.calculateTotalLength();
            }
            msg.setHeaders(headers);
            byte[] body = BytesUtils.getFromBuf(buf, totalLength);
            msg.setBody(body);
        }catch (Exception e){
            log.error("msg decoder failed:{}",e.getMessage());
            //todo: 抛异常
        }
        return msg;
    }
}
