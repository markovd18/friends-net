import { UserRelationshipVO } from "@markovda/fn-api";
import { Button } from "@mui/material";

type Props = {
    userData: UserRelationshipVO,
    onRemoveUser?: (username: string) => void,
}

const FriendsButtons: React.FC<Props> = ({userData, onRemoveUser}) => {

    return (
        <>
            <Button 
                variant="outlined"
                onClick={onRemoveUser ? () => onRemoveUser(userData.login) : undefined}
            >
                Remove friend
            </Button>
        </>
    )
}

export default FriendsButtons;