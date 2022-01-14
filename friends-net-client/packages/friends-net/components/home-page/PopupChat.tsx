import { Card } from "@mui/material"
import ChatMessage from "packages/friends-net/utils/messaging/ChatMessage"
import ChatFooter from "./ChatFooter"
import ChatHeader from "./ChatHeader"
import ChatMessageBox from "./ChatMessageBox"

type Props = {
    hidden?: boolean,
    chatWith?: string,
    messages: ChatMessage[],
    onClose?: () => void,
    onMessageSubmit?: (message: string) => void,
}

const PopupChat: React.FC<Props> = ({hidden, chatWith, messages, onClose, onMessageSubmit}) => {

    return (
        <Card 
            hidden={hidden} 
            elevation={3} 
            sx={{bottom: 0, right: 100, position: 'fixed', 
                minHeight: 400, minWidth: 310, maxWidth: 310
            }}
        >
            <ChatHeader chatWith={chatWith} onClose={onClose} />
            <ChatMessageBox messages={messages} />
            <ChatFooter onMessageSubmit={onMessageSubmit}/>
        </Card>
    )
}

export default PopupChat;