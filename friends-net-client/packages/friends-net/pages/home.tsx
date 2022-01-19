import { PostApi, PostVO, UserIdentificationDataVO } from "@markovda/fn-api";
import { Avatar, Button, Card, CardContent, CardHeader, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Divider, Fab, FormControlLabel, FormGroup, Stack, Switch, TextField, Tooltip, Typography } from "@mui/material";
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
import { useAuthHeader, useInterval } from "../hooks";
import NewReleasesIcon from '@mui/icons-material/NewReleases';
import AnnouncementIcon from "../components/AnnouncementIcon";
import PostList from "../components/home-page/PostList";
import { AddBox, AddCircle } from "@mui/icons-material";
import { hasAdminRole } from "../utils/authUtils";

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

    const [{name, login, roles}] = useUserData();
    const redirecting = useUnauthRedirect('/login');
    const authHeader = useAuthHeader();

    const fetchNewPosts = async () => {
        const latestPost = posts.at(0);
        let newPosts: PostVO[];
        if (latestPost) {
            newPosts = (await PostApi.findNewestPosts(undefined, latestPost.dateCreated, authHeader)).data;
        } else {
            newPosts = (await PostApi.findNewestPosts(10, undefined, authHeader)).data;
        }

        console.debug("new posts: ", newPosts);
        setPosts(prevState => newPosts.concat(prevState))
    }

    const fetchBasePosts = async () => {
        const posts = (await PostApi.findNewestPosts(10, undefined, authHeader)).data;
        console.debug("posts: ", posts);
        setPosts(() => posts);
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
            
        console.debug("went online:", wentOnline);
        console.debug("went offline:", wentOffline);
        
        setOnlineUsers(beforeOnline => {
            let newOnline = beforeOnline.filter(online => 
                wentOffline.find(offline => offline.login === online.login) === undefined);
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
                    <Card sx={{ maxWidth: 300, 
                         minWidth: 100, padding: 4, flex: 1, 
                        display: "flex", flexDirection: "column", 
                        justifyContent: "center", alignContent: "center",
                        position: "fixed"}}>
                        <CardContent>
                            <Stack direction={"row"} spacing={3}>
                                <Typography gutterBottom variant="h5" component="div">
                                    {name}
                                </Typography>
                                <Avatar sx={{width: 48, height: 48}}>{name?.charAt(0)}</Avatar>    
                            </Stack>
                            <Typography variant="body2" overflow="clip" gutterBottom>
                                {login}
                            </Typography>
                            <Button variant='contained' size='small' color='primary' onClick={() => setShowPostInput(true)}>
                                <AddCircle />
                                New post
                            </Button>
                        </CardContent>
                    </Card>

                    <Dialog open={showPostInput} onClose={() => setShowPostInput(false)}>
                        <DialogTitle>Create new Post</DialogTitle>
                        <DialogContent>
                            <DialogContent>
                                <DialogContentText gutterBottom>
                                    To create new post, enter it's content bellow.
                                </DialogContentText>
                                <TextField
                                    multiline
                                    rows={5}
                                    maxRows={10}
                                    fullWidth
                                    placeholder="What's on your mind?"
                                />
                            </DialogContent>
                            <DialogActions>
                                <FormGroup>
                                    {hasAdminRole(roles) && <FormControlLabel control={<Switch title="Announcement"/>} label="Announcement"/>}
                                </FormGroup>
                                <Button variant='outlined' onClick={() => setShowPostInput(false)}>Cancel</Button>
                                <Button variant='contained'>Create</Button>
                            </DialogActions>
                        </DialogContent>
                    </Dialog>

                    <PostList 
                        data={posts}
                        elevation={0}
                        style={{ 
                            minWidth: 200, padding: 4, flex: 1, 
                            display: "flex", flexDirection: "column", 
                            marginLeft: 40 }}
                    />

                    <OnlineUsersList data={onlineUsers} onItemClick={showChat}/>

                    <PopupChat 
                        hidden={chatHidden}
                        chatWith={chatWith?.name}
                        messages={getChatMessages()}
                        onClose={hideChat}
                        onMessageSubmit={handleMessageSubmit}
                    />
                </PageContentContainer>
            </main>
        </>
    )
}

export default HomePage;