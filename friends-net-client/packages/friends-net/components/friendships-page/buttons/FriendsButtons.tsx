import { UserRelationshipVO } from "@markovda/fn-api";
import { Button } from "@mui/material";

type Props = {
    userData: UserRelationshipVO,
    onRemoveUser?: (username: string) => void,
    onBlockUser?: (username: string) => void
}

const FriendsButtons: React.FC<Props> = ({userData, onRemoveUser, onBlockUser}) => {

    return (
        <>
            <Button 
                variant="outlined"
                onClick={onRemoveUser ? () => onRemoveUser(userData.login) : undefined}
            >
                Remove friend
            </Button>
            <Button
                variant="contained"
                color="error"
                onClick={onBlockUser ? () => onBlockUser(userData.login) : undefined}
            >
                Block user
            </Button>
        </>
    )
}

export default FriendsButtons;