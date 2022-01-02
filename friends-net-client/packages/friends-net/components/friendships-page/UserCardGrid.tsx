import { UserRelationshipVO } from "@markovda/fn-api";
import { Grid, Typography } from "@mui/material";
import { FriendshipsPageTab } from "../../utils/enums/RelationshipStatus";
import BlockedButtons from "./buttons/BlockedButtons";
import FriendRequestsButtons from "./buttons/FriendRequestsButtons";
import FriendsButtons from "./buttons/FriendsButtons";
import SearchResultButtons from "./buttons/SearchResultsButtons";
import UserSearchCard from "./UserSearchCard";

type UserCardGridProps = {
    data: UserRelationshipVO[],
    loggedInUsername: string,
    pageTab: FriendshipsPageTab,
    onSendFriendRequest?: (receiverLogin: string) => void,
    onBlockUser?: (blockedUserLogin: string) => void,
    onUnblockUser?: (unblockedUserLogin: string) => void,
    onRemoveUser?: (removedUserLogin: string) => void,
    onAcceptRequest?: (senderLogin: string) => void,
}

const UserCardGrid: React.FC<UserCardGridProps> = ({
    data,
    loggedInUsername,
    pageTab,
    onSendFriendRequest,
    onBlockUser,
    onUnblockUser,
    onRemoveUser,
    onAcceptRequest
}) => {

    return (!data || data.length === 0) 
    ? <Typography variant="h4">Nothing to show here...</Typography>
    :
    (
        <Grid container spacing={2} marginTop={1}>
            {data.map(userData => (
                <Grid item xs={3} key={userData.login}>
                    <UserSearchCard userData={userData}>
                        {pageTab === FriendshipsPageTab.SEARCH_RESULTS && 
                            <SearchResultButtons 
                                onSendFriendRequest={onSendFriendRequest}
                                loggedInUsername={loggedInUsername}
                                userData={userData}
                            />
                        }
                        {pageTab === FriendshipsPageTab.FRIEND_REQUESTS &&
                            <FriendRequestsButtons 
                                onAcceptRequest={onAcceptRequest}
                                userData={userData}
                                onBlockUser={onBlockUser}
                                onRemoveUser={onRemoveUser}
                            />
                        }
                        {pageTab === FriendshipsPageTab.FRIENDS && 
                            <FriendsButtons 
                                userData={userData}
                                onRemoveUser={onRemoveUser}
                                onBlockUser={onBlockUser}
                            />
                        }
                        {pageTab === FriendshipsPageTab.BLOCKED && 
                            <BlockedButtons 
                                userData={userData}
                                onUnblockUser={onUnblockUser}
                            />
                        }
                        
                    </UserSearchCard>
                </Grid>
            ))}
        </Grid>
    )
}

export default UserCardGrid;