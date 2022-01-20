import { UserIdentificationDataVO } from "@markovda/fn-api";
import { Client, IMessage } from "@stomp/stompjs";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { FriendStatus } from "../utils/enums/FriendStatus";
import ChatMessage from "../utils/messaging/ChatMessage";
import { IFriendStatusChangeMessage } from "../utils/messaging/FriendStatusChangeMessage";
import InboundChatMessage from "../utils/messaging/InboundChatMessage";
import OutboundChatMessage from "../utils/messaging/OutboundChatMessage";
import useAuthHeader from "./useAuthHeader";


const useMessagingConnection = (
    redirecting: boolean, 
    onInboundMessage: (message: InboundChatMessage) => void,
    onStatusChangeMessage: (statusChanges: IFriendStatusChangeMessage[]) => void,
): [
    sendMessage: (message: OutboundChatMessage) => void,
] => {
    
    const authHeader = useAuthHeader();

    const client = useRef<Client>();

    useEffect(() => {
        client.current = new Client({
            brokerURL: 'ws://localhost:8080/messaging/status-change',
            reconnectDelay: 5000, 
            onDisconnect: () => console.log("disconnected"),
            beforeConnect: () => { }
        });
    }, []);
    
    const convertStatusMessageToObject = (message: IMessage): IFriendStatusChangeMessage[] => {
        return JSON.parse(message.body);
    }

    const convertChatMessageToObject = (message: IMessage): InboundChatMessage => {
        return JSON.parse(message.body);
    }

    const processChatMessage = useCallback((message: IMessage) => {
        console.debug("inbound message:", message.body);
        const inboundMessage = convertChatMessageToObject(message);
        onInboundMessage(inboundMessage);
    }, [onInboundMessage]);
    
    const processFriendStatusChangeMessage = (message: IMessage) => {
        console.debug("message:", message.body);
        
        const statusList = convertStatusMessageToObject(message);
        onStatusChangeMessage(statusList);
    }

    const sendMessage = (message: OutboundChatMessage) => {
        client.current?.publish({headers: authHeader?.headers, destination: '/messaging/chat', body: JSON.stringify(message)})
    }

    const onConnect = useCallback(() => {
        if (!authHeader) return;
        client.current?.subscribe('/user/queue/friend-status', processFriendStatusChangeMessage, { 'Authorization': authHeader.headers.Authorization});
        client.current?.subscribe('/user/queue/chat', processChatMessage, { 'Authorization': authHeader.headers.Authorization})
    }, [processChatMessage]);
    
    useEffect(() => {
        if (!client.current) {
            return () => {};
        }

        if (redirecting || !authHeader || client.current?.active) {
            return () => {};
        }

        client.current.onConnect = onConnect;
        client.current.connectHeaders = { 'Authorization': authHeader.headers.Authorization};
    
        client.current.activate();
        return () => client.current?.deactivate();
    }, [redirecting]);

    return [sendMessage];
}

export default useMessagingConnection;