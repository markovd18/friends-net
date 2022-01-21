import { NewPostDataVO } from "@markovda/fn-api";
import { 
    Dialog, 
    DialogActions, 
    DialogContent,  
    DialogTitle, 
} from "@mui/material";
import React, { useState } from "react";
import NewPostModalActions from "./NewPostModalActions";
import NewPostModalContent from "./NewPostModalContent";


type Props = {
    open: boolean, 
    isAdmin: boolean,
    onClose: () => void,
    onSubmit: (data: FormData) => void,
}

type FormData = NewPostDataVO;

const NewPostModal: React.FC<Props> = ({open, isAdmin, onClose, onSubmit}) => {

    const [content, setContent] = useState<string>('');
    const [isAnnouncement, setIsAnnouncement] = useState<boolean>(false);
    const [error, setError] = useState<string>();

    const onContentChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
        setContent(event.target.value);
    }

    const getISOStringDateNow = () => {
        const isoStringNow = new Date().toISOString();
        return isoStringNow.substring(0, isoStringNow.length - 1);
    }

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (content.length < 5) {
            setError("Post content has to be at least 5 characters long");
            return;
        }

        setError(undefined);
        setContent('');
        onSubmit({content: content, isAnnouncement: isAnnouncement, dateCreated: getISOStringDateNow()});
    }

    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Create new Post</DialogTitle>
            <DialogContent>
                <DialogContent>
                    <NewPostModalContent 
                        content={content}
                        error={error}
                        onContentChange={onContentChange}
                    />
                </DialogContent>
                <DialogActions>
                    <NewPostModalActions 
                        isAdmin={isAdmin}
                        isAnnouncement={isAnnouncement}
                        onAnnouncementToggle={() => setIsAnnouncement(prevState => !prevState)}
                        onClose={onClose}
                        onSubmit={handleSubmit}
                    />
                </DialogActions>
            </DialogContent>
        </Dialog>
    )
}

export default NewPostModal;