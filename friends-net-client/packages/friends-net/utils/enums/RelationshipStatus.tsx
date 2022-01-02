export enum FriendshipsPageTab {
    FRIENDS = 'FRIENDS',
    FRIEND_REQUESTS = 'FRIEND_REQUESTS',
    BLOCKED = 'BLOCKED',
    SEARCH_RESULTS = 'SEARCH_RESULTS'
}

const params = {
    [FriendshipsPageTab.FRIEND_REQUESTS]: 'Friend requests',
    [FriendshipsPageTab.FRIENDS]: 'Friends',
    [FriendshipsPageTab.BLOCKED]: 'Blocked users',
    [FriendshipsPageTab.SEARCH_RESULTS]: 'Search results'
}

export const getCaption = (tab: FriendshipsPageTab): string => {
    return params[tab];
}