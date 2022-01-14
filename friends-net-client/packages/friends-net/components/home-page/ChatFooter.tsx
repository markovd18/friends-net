import { CardActions } from "@mui/material";
import SendMessageForm from "../SendMessageForm";

type Props = {
    onMessageSubmit?: (message: string) => void,
}

const ChatFooter: React.FC<Props> = ({onMessageSubmit}) => {
    return (
        <CardActions>
            <SendMessageForm onSubmit={onMessageSubmit}/>
        </CardActions>
    )
}

export default ChatFooter;