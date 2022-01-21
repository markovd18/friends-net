import { DialogContentText, TextField, FormHelperText } from "@mui/material";

type Props = {
    error?: string,
    onContentChange?: (event: React.ChangeEvent<HTMLTextAreaElement>) => void,
    content?: string,
}

const NewPostModalContent: React.FC<Props> = ({error, content, onContentChange}) => {
    return (
        <>
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
        </>
    )
}

export default NewPostModalContent;