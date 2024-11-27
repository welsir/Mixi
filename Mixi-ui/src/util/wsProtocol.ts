/*
 * @Author: Dhx
 * @Date: 2024-07-28 18:47:52
 * @Description:
 * @FilePath: \Mixi\Mixi-ui\src\util\wsProtocol.ts
 */
import Bytes from '@/util/byteUtil'
const VERSION_1 = 0x01
type SocketHeader = {
    type:number;
    data:string;
}
class SocketProtocol {
    private version:number
    private isHeartBeat:boolean = false
    private command:number
    private headers:SocketHeader[] = []
    private body:string = ''
    constructor(version:number,isHeartBeat:boolean,command:number,headers:SocketHeader[],body:string){
        this.version = version
        this.isHeartBeat = isHeartBeat
        this.command = command
        this.body = body
        this.headers = headers
    }
    decodeMessage(bytes:Bytes) {
        return this.decode(this.version,bytes)
    }
    encodeMessage() {
        return this.encode(this.version)
    }
    private encode(version:number){
        switch(version){
            case VERSION_1:{
                return this.encode_v1()
                break;
            }
            default: {
                console.error('version')
            }
        }
    }
    private decode(version:number,bytes:Bytes){
        switch(version){
            case VERSION_1:{
                return this.decode_v1(bytes)
                break;
            }
            default: {
                console.error('version')
            }
        }
    }
    private decode_v1(bytes:Bytes){
        this.version = bytes.readNumber(1)
        this.isHeartBeat = bytes.readBoolean()
        this.command = bytes.readNumber(1)
        let length = bytes.readVarInt()
        const headerCount = bytes.readNumber(1)
        for(let i=0;i<headerCount;i++){
            const headerDataLength = bytes.readVarInt()
            const headerType = bytes.readNumber(1)
            if(bytes.length()<headerDataLength){
                console.error('error')
                return null
            }
            console.log(headerDataLength)
            let str = bytes.readString(headerDataLength).replace(/(\w+)=/g, '"$1":')
            console.log(str)
            const header = JSON.parse(str)
            this.headers.push(header)
            length -=(bytes.computeVarInt32Size(headerDataLength)+headerDataLength+1)
        }
        if(length!=0){
            this.body = JSON.parse(bytes.readString(length))
        }
        return {
            headers:this.headers,
            body:this.body
        }
    }
    private encode_v1(){
        let headerLen = 0;
        for(let i=0;i<this.headers.length;i++){
            headerLen+=1+computeVarIntSize(this.headers[i].data.length)+this.headers[i].data.length;
        }
        var bodyLen = convertToUTF8Array(new TextEncoder().encode(this.body));
        var messageLen = headerLen + bodyLen.length
        var view = new DataView(new ArrayBuffer(8+this.headers.length+messageLen));
        let offset = 0;
        view.setInt8(offset++,VERSION_1);
        view.setInt8(offset++,this.isHeartBeat?1:0);
        view.setInt8(offset++,this.command);
        if(this.isHeartBeat)
            return view;
        offset = writeVarInt(messageLen,view,offset);
        view.setInt8(offset++,this.headers.length);
        for(let i=0;i<this.headers.length;i++){
            const headerData = convertToUTF8Array(new TextEncoder().encode(this.headers[i].data));
            offset = writeVarInt(headerData.length,view,offset);
            view.setInt8(offset++,this.headers[i].type);
            offset = writeByteArrayToView(headerData,view,offset);
        }
        if(bodyLen.length!=0){
            offset = writeByteArrayToView(bodyLen,view,offset);
        }
        printDataView(view)
        return view;
    }
}
export { SocketProtocol };
export type { SocketHeader };
function printDataView(view:any) {
    const byteArray = [];
    for (let i = 0; i < view.byteLength; i++) {
        byteArray.push(view.getInt8(i));
    }
    console.log(byteArray);
}
function writeByteArrayToView(bytes:any,view:any,offset:number){
    bytes.forEach((v:any)=>{
        view.setInt8(offset,v);
        offset++;
    })
    return offset;
}

function convertToUTF8Array(bytes:any){
    const res = new Int8Array(bytes.length);
    for (let i = 0; i < bytes.length; i++) {
        let value = bytes[i];
        // 将值调整到-128到127的范围
        res[i] = (value > 127) ? (value - 256) : value;
    }
    return res;
}
function writeVarInt(data:number,view:any,offset:number) {
    while (true) {
        if ((data & ~0x7F) == 0) {
            view.setInt8(offset++,data)
            break;
        } else {
            view.setUint8(offset++, (data & 0x7F) | 0x80);
            data >>>= 7;
        }
    }
    return offset;
}

function computeVarIntSize(value:number) {
    let i;
    for (i = 1; i < 5; i++) {
        // 创建一个掩码，该掩码将用于检查前 i 个字节的位
        const mask = 0xFFFFFFFF << (7 * i);
        if ((value & mask) === 0) {
            return i;
        }
    }
    return i;
}
