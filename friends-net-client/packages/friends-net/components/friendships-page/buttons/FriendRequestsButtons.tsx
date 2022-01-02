import { Button } from "@mui/material";
import { UserRelationshipVO } from '@markovda/fn-api'

type FriendRequestsButtonsProps = {
    onAcceptRequest?: (username: string) => void,
    onBlockUser?: (blockedUserLogin: string) => void,
    onRemoveUser?: (removedUserLogin: string) => void,
    userData: UserRelationshipVO,
}

const FriendRequestsButtons: React.FC<FriendRequestsButtonsProps> = ({
    onAcceptRequest,
    userData,
    onBlockUser,
    onRemoveUser
}) => {
    return (
        <>
            <Button 
                variant="contained" 
                onClick={onAcceptRequest ? () => onAcceptRequest(userData.login) : undefined}
            >
                Accept request
            </Button>
            <Button 
                variant="outlined"
                onClick={onRemoveUser ? () => onRemoveUser(userData.login) : undefined}
            >
                Delete request
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

export default FriendRequestsButtons;