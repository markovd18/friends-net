import { Stack, Typography, Avatar } from "@mui/material";

type Props = {
    name: string,
    login: string
}
const BaseCardUserIdentification: React.FC<Props> = ({name, login}) => {

    return (
        <>
            <Stack direction={"row"} spacing={3}>
                <Typography gutterBottom variant="h5" component="div">
                    {name}
                </Typography>
                <Avatar sx={{width: 48, height: 48}}>{name?.charAt(0)}</Avatar>    
            </Stack>
            <Typography variant="body2" overflow="clip" gutterBottom>
                {login}
            </Typography>
        </>
    )
}

export default BaseCardUserIdentification;