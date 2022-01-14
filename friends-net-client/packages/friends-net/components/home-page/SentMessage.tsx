import { ListItem, Typography } from "@mui/material";

type Props = {
    content: string
}

const SentMessage: React.FC<Props> = ({content}) => {
    return (
        <ListItem sx={{display: 'flex', justifyContent: 'right'}}>
            <Typography sx={{
                backgroundColor: 'blue', padding: 1, borderRadius: 5, 
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

export default SentMessage;