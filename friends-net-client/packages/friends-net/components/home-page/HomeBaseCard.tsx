import { AddCircle } from "@mui/icons-material";
import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import { Card, CardContent, Stack, Typography, Avatar, Button } from "@mui/material";

type Props = {
    name: string,
    login: string,
    isAdmin?: boolean,
    onNewPostClick: () => void,
}

const HomeBaseCard: React.FC<Props> = ({name, login, isAdmin, onNewPostClick}) => {

    return (
        <Card sx={{ maxWidth: 300, 
            minWidth: 100, padding: 4, flex: 1, 
           display: "flex", flexDirection: "column", 
           justifyContent: "center", alignContent: "center",
           position: "fixed"}}>
           <CardContent>
                <Stack direction={"row"} spacing={3}>
                    <Typography gutterBottom variant="h5" component="div">
                        {name}
                    </Typography>
                    <Avatar sx={{width: 48, height: 48}}>{name?.charAt(0)}</Avatar>    
                </Stack>
                <Typography variant="body2" overflow="clip" gutterBottom>
                    {login}
                </Typography>
                <Stack spacing={2}>
                    <Button variant='contained' size='small' color='primary' onClick={onNewPostClick}>
                        <AddCircle />
                        New post
                    </Button>
                    {isAdmin && 
                    <Button variant='contained' size='small' color='secondary'>
                        <AdminPanelSettingsIcon />
                        Set admin roles
                    </Button>}
                </Stack>
           </CardContent>
       </Card>
    )
}

export default HomeBaseCard;