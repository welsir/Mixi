import {defineStore} from 'pinia';
import MixiWebSocket from "@/util/webSocket";

export const chatStore = defineStore('chat', {
    state: () => ({
        roomName: '',
        uid: '',
        roomId: 0,
        ws: null as MixiWebSocket | null,
    }),
    actions: {
        setChatDetails(roomName: string, uid: number,roomId: number) {
            this.roomName = roomName;
            this.uid = uid;
            this.roomId = roomId;
        },
        createWsIfAbsent(){
            if(!this.ws){
                this.ws = new MixiWebSocket('ws://localhost:8090/chat');
                this.ws.init();
            }
            return this.ws;
        },
    },
});
