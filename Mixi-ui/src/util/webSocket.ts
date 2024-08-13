/*
 * @Author: Dhx
 * @Date: 2024-07-22 16:05:16
 * @Description: 
 * @FilePath: \Mixi\Mixi-ui\src\util\webSocket.ts
 */
class MixiWebSocket {
    private url: string = ''
    private ws: WebSocket | undefined
    private onopenFunc: Function = () => {
        console.log("websocket open")
    }
    private onmessageFunc: Function = (event: any) => {
        console.log("remote message:" + event.data)
    }
    private onerrorFunc: Function = () => {
        console.error("websocket error")
    }
    private oncloseFunc: Function = () => {
        console.log("websocket closed")
    }
    constructor(url:string) {
        this.url = url
    }
    // 成功连接后触发
    onopen(func=()=>{}) {
        this.onopenFunc = func
    }
    // 接收来自服务端的消息后触发
    onmessage(func = (event:any) => {}) {
        this.onmessageFunc = func
    }
    // 发生错误时触发
    onerror(func=()=>{}) {
        this.onerrorFunc = func
    }
    // 关闭连接后触发
    onclose(func=()=>{}) {
        this.oncloseFunc = func
    }
    // 开始连接
    init() {
        if (this.ws != null) return
        this.ws = new WebSocket(this.url)
        this.ws.onopen = () => {
            this.onopenFunc()
        };
        this.ws.onmessage = (event) => {
            this.onmessageFunc(event)
        };
        this.ws.onerror = (error) => {
            this.onerrorFunc(error)
        };
        this.ws.onclose = () => {
            this.oncloseFunc()
        };
    }
    send(msg:DataView) {
        if(this.ws!.readyState == WebSocket.OPEN) {
            this.ws!.send(msg)
        }
    }
    close() {
        this.ws!.close()
    }
}
export default MixiWebSocket