import { AdminApi, EnumUserRole, NewPostDataVO, PostApi, PostVO, UserIdentificationDataVO, UserSearchApi } from "@markovda/fn-api";
import { NextPage } from "next";
import Head from "next/head";
import { useCallback, useEffect, useState } from "react";
import OnlineUsersList from "../components/home-page/OnlineUsersList";
import Navbar from "../components/nav/Navbar";
import PageContentContainer from "../components/PageContentContainer";
import useUnauthRedirect from "../hooks/useUnauthRedirect";
import useUserData from "../hooks/useUserData";
import useMessagingConnection from "../hooks/useMessagingConnection";
import PopupChat from "../components/home-page/PopupChat";
import ChatMessage from "../utils/messaging/ChatMessage";
import OutboundChatMessage from "../utils/messaging/OutboundChatMessage";
import InboundChatMessage from "../utils/messaging/InboundChatMessage";
import { IFriendStatusChangeMessage } from "../utils/messaging/FriendStatusChangeMessage";
import { FriendStatus } from "../utils/enums/FriendStatus";
import { useAuthHeader, useInterval, useSnackbar, useUserRolesDialog } from "../hooks";
import PostList from "../components/home-page/PostList";
import { hasAdminRole } from "../utils/authUtils";
import NewPostModal from "../components/home-page/NewPostModal";
import HomeBaseCard from "../components/home-page/HomeBaseCard";
import { Stack } from "@mui/material";

type Messages = {
    [login: string]: ChatMessage[]
}

const HomePage: NextPage = () => {
    
    const [chatHidden, setChatHidden] = useState<boolean>(true);
    const [chatWith, setChatWith] = useState<UserIdentificationDataVO | undefined>();
    const [onlineUsers, setOnlineUsers] = useState<UserIdentificationDataVO[]>([]);
    const [messages, setMessages] = useState<Messages>({});
    const [lastInboundMessage, setLastInboundMessage] = useState<InboundChatMessage>();
    const [posts, setPosts] = useState<PostVO[]>([]);
    const [showPostInput, setShowPostInput] = useState(false);

    const [{name, login, roles},,logoutUser] = useUserData();
    const redirecting = useUnauthRedirect('/login');
    const authHeader = useAuthHeader();
    const [Snackbar, showSnackbar] = useSnackbar();

    const handleAdminToggled = async (login: string, isAdmin: boolean) => {
        try {
            if (isAdmin) {
                await AdminApi.addRoleToUser(EnumUserRole.ADMIN, login, authHeader);
            } else {
                await AdminApi.removeRoleFromUser(EnumUserRole.ADMIN, login, authHeader);
            }
        } catch(error) {
            handleError(error);
        }
    }

    const [adminDialog, showAdminDialog] = useUserRolesDialog(handleAdminToggled);

    const fetchFriendsWithRoles = async () => {
        const friends = (await UserSearchApi.findFriendsWithRoles(authHeader)).data;
        return friends.map(friend => ({
            login: friend.login, 
            name: friend.name, 
            isAdmin: friend.roles ? friend.roles.some(role => role === EnumUserRole.ADMIN) : false
        }));
    }

    const handleShowAdminDialog = async () => {
        try {
            const data = await fetchFriendsWithRoles();
            showAdminDialog(data);
        } catch (error) {
            handleError(error);
        }
    }

    const fetchNewPosts = async () => {
        const latestPost = posts.at(0);
        let newPosts: PostVO[];
        if (latestPost) {
            newPosts = (await PostApi.findNewestPosts(undefined, latestPost.dateCreated, authHeader)).data;
        } else {
            newPosts = (await PostApi.findNewestPosts(10, undefined, authHeader)).data;
        }

        setPosts(prevState => newPosts.concat(prevState))
    }

    const fetchBasePosts = async () => {
        try {
            const posts = (await PostApi.findNewestPosts(10, undefined, authHeader)).data;
            setPosts(() => posts);
        } catch (error) {
            handleError(error);
        }
    }

    const handleError = useCallback((error) => {
        if (!error.response) {
            showSnackbar('Server not responding. Please try again later.', 'error');
            return;
        }

        switch(error.response.status) {
            case 401:
                logoutUser();
                break;
            case 400:
                showSnackbar('Invalid request sent. Please try again.', 'warning');
                break;
            default:
                showSnackbar('Unknown error while creating post. Please try again later.', 'warning');
        }
    }, []);

    const onPostSubmit = async (data: NewPostDataVO) => {
        setShowPostInput(false);
        try {
            await PostApi.createNewPost(data, authHeader);
            await fetchNewPosts();
        } catch (error) {
            handleError(error);
        }
    }

    useInterval(fetchNewPosts, 20000);

    useEffect(() => {
        if (!redirecting && authHeader) {
            fetchBasePosts();
        }
    }, []);
    
    const showChat = (userData: UserIdentificationDataVO) => {
        setChatWith(userData);
        setChatHidden(false);
    }
    
    const hideChat = () => {
        setChatHidden(true);
        setChatWith(undefined);
    }
    
    const addMessage = (login: string, message: ChatMessage) => {
        setMessages(prevState => {
            const newState = {...prevState};
            if (!newState[login]) {
                newState[login] = [];
            }
            newState[login].push(message);
            return newState;
        });
    }

    const getUserByLogin = (login: string) => {
        return onlineUsers.find(user => user.login === login);
    }
    
    const getChatMessages = () => {
        if (!chatWith || !messages[chatWith.login]) return [];
        return messages[chatWith.login];
    }
    
    const onInboundMessage = useCallback((message: InboundChatMessage) => {
        addMessage(message.from, message);
        setLastInboundMessage(() => message);
    }, []);

    useEffect(() => {
        if (chatHidden && lastInboundMessage) {
            const from = getUserByLogin(lastInboundMessage.from);
            if (from) {
                showChat(from);
            }
        }
    }, [lastInboundMessage]);

    const onStatusChangeMessage = (statusChanges: IFriendStatusChangeMessage[]) => {
        let wentOnline: UserIdentificationDataVO[] = [];
        let wentOffline: UserIdentificationDataVO[] = [];
        statusChanges.forEach(friend => friend.status === FriendStatus.ONLINE ? 
            wentOnline.push(friend) : 
            wentOffline.push(friend));
        
        setOnlineUsers(beforeOnline => {
            let newOnline = beforeOnline.filter(online => 
                wentOffline.find(offline => offline.login === online.login) === undefined);
            wentOnline = wentOnline.filter(online => newOnline.find(user => user.login === online.login) === undefined);
            return newOnline.concat(wentOnline);
        });
    }
    
    const [sendMessage] = useMessagingConnection(redirecting, onInboundMessage, onStatusChangeMessage);

    const handleMessageSubmit = (message: string) => {
        if (chatWith) {
            const outboundMessage: OutboundChatMessage = {to: chatWith.login, content: message};
            addMessage(outboundMessage.to, outboundMessage);
            sendMessage(outboundMessage);
        }
    }
    
    return redirecting ? null : (
        <>
            <Head>
                <title>Friends Net</title>
                <meta name='description' content="Friends Net user's wallboard page"/>
            </Head>

            <main>
                <Navbar />
                <PageContentContainer>
                    <Stack direction={{xs: "column", sm: "row"}} alignItems={{xs: "center", sm: "normal"}}>

                        <Stack direction={"column"} position={{xs: "static", sm: "fixed"}}>
                            <HomeBaseCard 
                                name={name}
                                login={login}
                                onNewPostClick={() => setShowPostInput(true)}
                                isAdmin={hasAdminRole(roles)}
                                onAdminRolesClick={handleShowAdminDialog}
                            />

                            <OnlineUsersList data={onlineUsers} onItemClick={showChat}/>
                        </Stack>
                        <PostList 
                            data={posts}
                            style={{ 
                                minWidth: {xs: '90%', sm: 250}, flex: 1, 
                                display: "flex", flexDirection: "column", 
                                marginLeft: {xs: 0, sm: 40} }}
                        />
                    </Stack>

                    <NewPostModal 
                        open={showPostInput}
                        isAdmin={hasAdminRole(roles)}
                        onClose={() => setShowPostInput(false)}
                        onSubmit={onPostSubmit}
                    />
                    {adminDialog}
                    <PopupChat 
                        hidden={chatHidden}
                        chatWith={chatWith?.name}
                        messages={getChatMessages()}
                        onClose={hideChat}
                        onMessageSubmit={handleMessageSubmit}
                    />
                    {Snackbar}
                </PageContentContainer>
            </main>
        </>
    )
}

export default HomePage;