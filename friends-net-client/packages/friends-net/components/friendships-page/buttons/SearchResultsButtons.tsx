import { EnumRelationshipStatus, UserRelationshipVO } from "@markovda/fn-api";
import { Button } from "@mui/material";

type SearchResultButtonsProps = {
    onSendFriendRequest?: (receiverLogin: string) => void,
    onRemoveUser?: (username: string) => void,
    loggedInUsername: string,
    userData: UserRelationshipVO
}

const SearchResultButtons: React.FC<SearchResultButtonsProps> = ({
    onSendFriendRequest, 
    onRemoveUser,
    loggedInUsername, 
    userData 
}) => {

    return (
        <>
            {userData.login === loggedInUsername 
            ? <Button variant="outlined" disabled>Me</Button>
            : 
            userData.relationshipStatus === EnumRelationshipStatus.REQUEST_SENT 
            ? 
            <>
                <Button variant="outlined" disabled>Request pending</Button>
                <Button 
                    variant="contained"
                    onClick={onRemoveUser ? () => onRemoveUser(userData.login) : undefined}    
                >
                    Cancel request
                </Button>
            </>
            : <Button 
                variant="contained" 
                onClick={onSendFriendRequest ? () => onSendFriendRequest(userData.login) : undefined}
            >
                Send friend request
            </Button>
            }
        </>
    )
}

export default SearchResultButtons;