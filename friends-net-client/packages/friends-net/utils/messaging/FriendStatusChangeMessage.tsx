import { FriendStatus } from "../enums/FriendStatus";

export interface IFriendStatusChangeMessage {

    login: string,

    name: string,

    status: FriendStatus,
}