
export default interface ChatMessage {
    content: string,
}

export const instanceOfChatMessage = (object: any): object is ChatMessage => {
    return 'content' in object;
}