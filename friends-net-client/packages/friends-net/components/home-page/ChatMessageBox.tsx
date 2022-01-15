import { CardContent, List } from "@mui/material";
import { instanceOfOutbound } from "../../utils/messaging/OutboundChatMessage";
import ChatMessage from "../../utils/messaging/ChatMessage";
import SentMessage from "./SentMessage";
import ReceivedMessage from "./ReceivedMessage";
import { useMemo } from "react";

type Props = {
    messages: ChatMessage[]
}

const renderMessage = (message: ChatMessage, index: number) => {
    return instanceOfOutbound(message) ? 
        <SentMessage key={index} content={message.content} /> :
        <ReceivedMessage key={index} content={message.content} />;
}

const ChatMessageBox: React.FC<Props> = ({messages}) => {

    return (
        <CardContent>
            <List sx={{maxHeight: 240, minHeight: 240, overflow: 'auto'}}>
                {messages.map((message, index) => renderMessage(message, index))}
            </List>
        </CardContent>
    )
}

export default ChatMessageBox;