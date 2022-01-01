export enum FriendshipsPageTab {
    FRIENDS = 'FRIENDS',
    FRIEND_REQUESTS = 'FRIEND_REQUESTS',
    BLOCKED = 'BLOCKED'
}

const params = {
    [FriendshipsPageTab.FRIEND_REQUESTS]: 'Friend requests',
    [FriendshipsPageTab.FRIENDS]: 'Friends',
    [FriendshipsPageTab.BLOCKED]: 'Blocked users'
}

export const getCaption = (tab: FriendshipsPageTab): string => {
    return params[tab];
}