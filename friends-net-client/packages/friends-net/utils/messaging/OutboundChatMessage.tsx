import ChatMessage, { instanceOfChatMessage } from "./ChatMessage";

export default interface OutboundChatMessage extends ChatMessage {
    to: string,
}

export const instanceOfOutbound = (object: any): object is OutboundChatMessage => {
    return 'to' in object && instanceOfChatMessage(object);
}