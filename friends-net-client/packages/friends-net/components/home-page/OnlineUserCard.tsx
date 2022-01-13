import { UserIdentificationDataVO } from "@markovda/fn-api";
import { Avatar, Badge, Card, CardActionArea, CardContent, Stack, Typography } from "@mui/material";
import OnlineUserBadge from "./OnlineUserBadge";

type Props = {
    user: UserIdentificationDataVO,
    onClick?: (login: string) => void,
}

const OnlineUserCard: React.FC<Props> = ({user, onClick}) => {

    const handleClick = () => {
        if (onClick) {
            onClick(user.login);
        }
    }

    return (
        <Card elevation={0}>
            <CardActionArea onClick={handleClick} >
                <CardContent>
                    <Stack direction="row" spacing={2}>
                        <OnlineUserBadge
                            overlap="circular"
                            anchorOrigin={{ vertical: 'bottom', horizontal: 'right'}}
                            variant='dot'
                        >
                            <Avatar>
                                {user.name.charAt(0)}
                            </Avatar>
                        </OnlineUserBadge>
                        <Typography variant="h6" color='text.secondary'>
                            {user.name}
                        </Typography>
                    </Stack>
                </CardContent>
            </CardActionArea>
        </Card>
    )
}

export default OnlineUserCard;