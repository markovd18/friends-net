import { UserIdentificationDataVO } from "@markovda/fn-api";
import { Avatar, ListItem, ListItemButton, ListItemIcon, ListItemText } from "@mui/material";
import OnlineUserBadge from "./OnlineUserBadge";

type Props = {
    userData: UserIdentificationDataVO,
    onClick?: (userData: UserIdentificationDataVO) => void,
}

const OnlineUserCard: React.FC<Props> = ({userData, onClick}) => {

    const handleClick = () => {
        if (onClick) {
            onClick(userData);
        }
    }

    return (
        <ListItem component="div">
            <ListItemButton onClick={handleClick}>
                <ListItemIcon>
                    <OnlineUserBadge
                        overlap="circular"
                        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                        variant="dot"
                    >
                        <Avatar>
                            {userData.name.charAt(0)}
                        </Avatar>
                    </OnlineUserBadge>
                </ListItemIcon>
                <ListItemText primary={userData.name}/>
            </ListItemButton>
        </ListItem>
    )
}

export default OnlineUserCard;