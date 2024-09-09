/*
 * @Author: Dhx
 * @Date: 2024-07-31 01:01:53
 * @Description:
 * @FilePath: \Mixi\Mixi-ui\src\util\socketMessage.ts
 */
import {SocketProtocol,type SocketHeader} from '@/util/wsProtocol'
import type Bytes from './byteUtil'
const VERSION_1 = 0x01
const CMD_1 = 0x01
const HEADER_TYPE_COMPRESS = 0X01
const HEADER_TYPE_CHATROOM = 0X02
const HEADER_TYPE_AUTH = 0X03
const HEADER_CMD_JOIN = 0x0a
const HEADER_CMD_MESSAGE = 0x0c

function heartBeatMessage() {
    return new SocketProtocol(VERSION_1,true,CMD_1,[],"").encodeMessage()!
}
function joinRoomMessage(msg:{roomId:number,uid:number}){
    const header:SocketHeader = {
        type: HEADER_TYPE_CHATROOM,
        data: JSON.stringify({
            room:msg.roomId,
            uid:msg.uid,
            cmd:HEADER_CMD_JOIN.toString()
        })
    }
    return new SocketProtocol(VERSION_1,false,CMD_1,[header],"").encodeMessage()!
}
function chatMessage(msg:{roomId:number,uid:number,content:string}) {
    const header:SocketHeader = {
        type: HEADER_TYPE_CHATROOM,
        data: JSON.stringify({
            room:msg.roomId,
            uid:msg.uid,
            cmd:HEADER_CMD_MESSAGE.toString()
        })
    }
    const body = JSON.stringify({
        roomId:msg.roomId,
        fromUid:msg.uid,
        content:msg.content
    })
    return new SocketProtocol(VERSION_1,false,CMD_1,[header],body).encodeMessage()!
}

function queryHistoryMessage(msg:{roomId:number}){
    const header:SocketHeader = {
        type: HEADER_TYPE_CHATROOM,
        data: JSON.stringify({
            room:msg.roomId,
            cmd:HEADER_CMD_MESSAGE.toString()
        })
    }
    return new SocketProtocol(VERSION_1,false,CMD_1,[],"").encodeMessage();
}
function decodeRemoteMessage(bytes:Bytes){
    return new SocketProtocol(VERSION_1,false,CMD_1,[],"").decodeMessage(bytes)
}
export {heartBeatMessage,joinRoomMessage,chatMessage,decodeRemoteMessage,queryHistoryMessage}
