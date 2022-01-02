import { UserRelationshipVO } from "@markovda/fn-api";
import { Button } from "@mui/material";

type Props = {
    userData: UserRelationshipVO,
    onUnblockUser?: (username: string) => void
}

const BlockedButtons: React.FC<Props> = ({userData, onUnblockUser}) => {

    return (
        <>
            <Button
                variant="outlined"
                onClick={onUnblockUser ? () => onUnblockUser(userData.login) : undefined}
            >
                Unblock user
            </Button>
        </>
    )
}

export default BlockedButtons;