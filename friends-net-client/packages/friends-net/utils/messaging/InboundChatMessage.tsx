import ChatMessage, { instanceOfChatMessage } from "./ChatMessage";

export default interface InboundChatMessage extends ChatMessage {
    from: string,
}

export const instanceOfInbound = (object: any): object is InboundChatMessage => {
    return 'from' in object && instanceOfChatMessage(object);
}