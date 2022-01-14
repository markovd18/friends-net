import { CardHeader, IconButton } from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';

type Props = {
    chatWith?: string,
    onClose?: () => void,
}

const ChatHeader: React.FC<Props> = ({chatWith, onClose}) => {
    return (
        <CardHeader 
            title={chatWith} 
            titleTypographyProps={{color: 'white', variant: 'h5'}} 
            sx={{backgroundColor: '#1976d2'}}
            action={
                <IconButton onClick={onClose}>
                    <CloseIcon htmlColor="white" titleAccess="Close chat"/>
                </IconButton>
            }    
        />
    )
}

export default ChatHeader;