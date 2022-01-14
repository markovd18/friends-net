import { CardContent, List } from "@mui/material";
import { instanceOfOutbound } from "../../utils/messaging/OutboundChatMessage";
import ChatMessage from "../../utils/messaging/ChatMessage";
import SentMessage from "./SentMessage";
import ReceivedMessage from "./ReceivedMessage";

type Props = {
    messages: ChatMessage[]
}

const renderMessage = (message: ChatMessage) => {
    return instanceOfOutbound(message) ? 
        <SentMessage content={message.content} /> :
        <ReceivedMessage content={message.content} />;
}

const ChatMessageBox: React.FC<Props> = ({messages}) => {
    return (
        <CardContent>
            <List sx={{maxHeight: 240, minHeight: 240, overflow: 'auto'}}>
                {messages.map(message => renderMessage(message))}
            </List>
        </CardContent>
    )
}

export default ChatMessageBox;