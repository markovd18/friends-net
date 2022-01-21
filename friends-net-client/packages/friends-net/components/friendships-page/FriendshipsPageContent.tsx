import { UserRelationshipVO } from "@markovda/fn-api"
import { Box, CircularProgress, Typography } from "@mui/material"
import { FriendshipsPageTab } from "../../utils/enums/RelationshipStatus"
import UserCardGrid from './UserCardGrid'

type Props = {
    isDataLoading?: boolean,
    showingSearchResults?: boolean,
    lastSearchString?: string,
    lastSearchResult: UserRelationshipVO[],
    activeTab: FriendshipsPageTab,
    loggedInUsername: string,
    onSendFriendRequest?: (receiverLogin: string) => void,
    onBlockUser?: (blockedUserLogin: string) => void,
    onUnblockUser?: (unblockedUserLogin: string) => void,
    onRemoveUser?: (removedUserLogin: string) => void,
    onAcceptRequest?: (senderLogin: string) => void,
}

const FriendshipsPageContent: React.FC<Props> = ({
    isDataLoading,
    showingSearchResults,
    lastSearchString,
    lastSearchResult,
    activeTab,
    loggedInUsername,
    onSendFriendRequest,
    onBlockUser,
    onUnblockUser,
    onRemoveUser,
    onAcceptRequest
}) => {

    const renderLoading = () => {
        return (
            <Box sx={{display: 'flex'}}>
                <CircularProgress />
            </Box>
        );
    }

    const renderSearchResultHeader = () => {
        return (
            <Typography variant="h4">
                Search results for "{lastSearchString}":
            </Typography>
        )
    }

    return (
        isDataLoading 
        ? 
        renderLoading()
        : 
        <>
            {showingSearchResults && renderSearchResultHeader()}
            <UserCardGrid 
                data={lastSearchResult}
                loggedInUsername={loggedInUsername}
                pageTab={activeTab}
                onSendFriendRequest={onSendFriendRequest}
                onRemoveUser={onRemoveUser}
                onAcceptRequest={onAcceptRequest}
                onBlockUser={onBlockUser}
                onUnblockUser={onUnblockUser}
            />
        </>
        
    )
}

export default FriendshipsPageContent;