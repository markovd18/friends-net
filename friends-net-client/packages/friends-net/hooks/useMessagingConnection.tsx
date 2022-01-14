import { UserIdentificationDataVO } from "@markovda/fn-api";
import { Client, IMessage } from "@stomp/stompjs";
import { useEffect, useMemo, useState } from "react";
import { FriendStatus } from "../utils/enums/FriendStatus";
import ChatMessage from "../utils/messaging/ChatMessage";
import { IFriendStatusChangeMessage } from "../utils/messaging/FriendStatusChangeMessage";
import InboundChatMessage from "../utils/messaging/InboundChatMessage";
import OutboundChatMessage from "../utils/messaging/OutboundChatMessage";
import useAuthHeader from "./useAuthHeader";


let client = new Client({
    brokerURL: 'ws://localhost:8080/messaging/status-change',
    reconnectDelay: 5000, 
    onDisconnect: () => console.log("disconnected"),
    beforeConnect: () => { }
});


const useMessagingConnection = (redirecting: boolean): [
    UserIdentificationDataVO[], 
    (message: OutboundChatMessage) => void,
    ChatMessage[],
] => {
    
    const [onlineUsers, setOnlineUsers] = useState<UserIdentificationDataVO[]>([]);
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const authHeader = useAuthHeader();    
    
    const convertStatusMessageToObject = (message: IMessage): IFriendStatusChangeMessage[] => {
        return JSON.parse(message.body);
    }

    const convertChatMessageToObject = (message: IMessage): InboundChatMessage => {
        return JSON.parse(message.body);
    }

    const processChatMessage = (message: IMessage) => {
        console.debug("inbound message:", message.body);
        const inboundMessage = convertChatMessageToObject(message);
        setMessages(prevState => prevState.concat(inboundMessage));
    }
    
    const processFriendStatusChangeMessage = (message: IMessage) => {
        console.log("message:", message.body);
        
        const statusList = convertStatusMessageToObject(message);
        let wentOnline: UserIdentificationDataVO[] = [];
        let wentOffline: UserIdentificationDataVO[] = [];
        statusList.forEach(friend => friend.status === FriendStatus.ONLINE ? 
            wentOnline.push(friend) : 
            wentOffline.push(friend));
            
        console.debug("went online:", wentOnline);
        console.debug("went offline:", wentOffline);
        
        setOnlineUsers(beforeOnline => {
            let newOnline = beforeOnline.filter(online => 
                wentOffline.find(offline => offline.login === online.login) === undefined);
            return newOnline.concat(wentOnline);
        });
    }

    const sendMessage = (message: OutboundChatMessage) => {
        setMessages(prevState => prevState.concat(message))
        client.publish({headers: authHeader?.headers, destination: '/messaging/chat', body: JSON.stringify(message)})
    }
    
    useEffect(() => {
        if (redirecting || !authHeader) {
            return () => {};
        }

        const onConnect = () => {
            client.subscribe('/user/queue/friend-status', processFriendStatusChangeMessage, { 'Authorization': authHeader.headers.Authorization});
            client.subscribe('/user/queue/chat', processChatMessage, { 'Authorization': authHeader.headers.Authorization})
        }

        client.onConnect = onConnect;
        client.connectHeaders = { 'Authorization': authHeader.headers.Authorization};
    
        client.activate();
        return () => client.deactivate();
    }, [redirecting]);

    return [onlineUsers, sendMessage, messages];
}

export default useMessagingConnection;