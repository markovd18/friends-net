import { UserIdentificationDataVO } from "@markovda/fn-api";
import { Card, CardMedia, Avatar, Box, CardContent, Stack, CardActions, Button, Typography } from "@mui/material";
import { PropsWithChildren } from "react";

type Props = Required<PropsWithChildren<{}>> & {
    userData: UserIdentificationDataVO
}

const UserSearchCard: React.FC<Props> = ({userData, children}) => {
    return (
        <Card elevation={2}>
            <CardMedia sx={{alignItems: 'center', justifyContent: 'center', display: 'flex'}}>
                <Box paddingTop={2}>
                    <Avatar>
                        {userData.name.charAt(0)}
                    </Avatar>
                </Box>
            </CardMedia>
            <CardContent>
                <Typography variant="h5">
                    {userData.name}
                </Typography>
                <Typography variant="body2" color={'text.secondary'}>
                    {userData.login}
                </Typography>
            </CardContent>
            <CardActions sx={{alignItems: 'center', justifyContent: 'center', display: 'flex'}}>
                <Stack spacing={1}>
                    {children}
                </Stack>
            </CardActions>
        </Card>
    )
}

export default UserSearchCard;