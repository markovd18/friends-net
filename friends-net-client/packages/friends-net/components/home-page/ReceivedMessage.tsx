import { ListItem, Typography } from "@mui/material";

type Props = {
    content: string
}

const ReceivedMessage: React.FC<Props> = ({content}) => {
    return (
        <ListItem sx={{display: 'flex', justifyContent: 'flex-start'}}>
            <Typography sx={{
                backgroundColor: 'GrayText', padding: 1, borderRadius: 5, 
                whiteSpace: 'normal', overflow: 'auto', maxWidth: 200
                }} 
                color={'white'} 
                variant='body2'
            >
                {content}
            </Typography>
        </ListItem>
    )
}

export default ReceivedMessage;