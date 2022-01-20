import { NewPostDataVO } from "@markovda/fn-api";
import { 
    Button, 
    ButtonGroup, 
    Dialog, 
    DialogActions, 
    DialogContent, 
    DialogContentText, 
    DialogTitle, 
    FormControl, 
    FormControlLabel, 
    FormGroup, 
    FormHelperText, 
    Stack, 
    Switch, 
    TextField 
} from "@mui/material";
import { Box } from "@mui/system";
import React, { useState } from "react";


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

    const renderAnnouncementSwitch = (): JSX.Element => {
        return (
            <Switch 
                title="Announcement" 
                checked={isAnnouncement} 
                onChange={() => setIsAnnouncement(prevState => !prevState)} 
            />
        );
    }

    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>Create new Post</DialogTitle>
            <DialogContent>
                <DialogContent>
                    <DialogContentText gutterBottom>
                        To create new post, enter it's content bellow.
                    </DialogContentText>
                    
                        <TextField
                            multiline
                            minRows={5}
                            maxRows={10}
                            fullWidth
                            placeholder="What's on your mind?"
                            value={content}
                            onChange={onContentChange}
                            error={!!error}
                        />
                        {error && <FormHelperText id="error-desc" error>{error}</FormHelperText>}
                    
                </DialogContent>
                <DialogActions>
                    <form onSubmit={handleSubmit}>
                        <Stack direction='row'>
                            <FormGroup>
                                {isAdmin && 
                                <FormControlLabel control={renderAnnouncementSwitch()} label="Announcement"/>}
                            </FormGroup>
                            <ButtonGroup >
                                <Button variant='outlined' onClick={onClose}>Cancel</Button>
                                <Button variant='contained' type='submit'>Create</Button>
                            </ButtonGroup>
                        </Stack>
                    </form>
                </DialogActions>
            </DialogContent>
        </Dialog>
    )
}

export default NewPostModal;