import { IconButton, TextField } from "@mui/material";
import React, { useState } from "react";
import SendIcon from '@mui/icons-material/Send';

type Props = {
    onSubmit?: (message: string) => void,
}

const SendMessageForm: React.FC<Props> = ({onSubmit}) => {

    const [messageText, setMessageText] = useState("");

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (onSubmit && messageText.length > 0) {
            onSubmit(messageText);
            setMessageText("");
        }
    }

    const handleMessageChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        event.preventDefault();
        setMessageText(event.target.value);
    }

    return (
        <form onSubmit={handleSubmit}>
            <TextField 
                size="small" 
                placeholder="Type message here"
                value={messageText}
                onChange={handleMessageChange}
            ></TextField>
            <IconButton type="submit">
                <SendIcon color="primary"/>
            </IconButton>
        </form>
    )
}

export default SendMessageForm;