import { UserIdentificationDataVO } from "@markovda/fn-api";
import { Avatar, Card, CardContent, Stack, Typography } from "@mui/material";
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

type Messages = {
    [login: string]: ChatMessage[]
}

const HomePage: NextPage = () => {
    
    const [chatHidden, setChatHidden] = useState<boolean>(true);
    const [chatWith, setChatWith] = useState<UserIdentificationDataVO | undefined>();
    const [onlineUsers, setOnlineUsers] = useState<UserIdentificationDataVO[]>([]);
    const [messages, setMessages] = useState<Messages>({});
    const [lastInboundMessage, setLastInboundMessage] = useState<InboundChatMessage>();
    const [name, login] = useUserData();
    const redirecting = useUnauthRedirect('/login');
    
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
                        minHeight: 100, minWidth: 100, padding: 4, flex: 1, 
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
                            <Typography variant="body2" overflow="clip">
                                {login}
                            </Typography>
                        </CardContent>

                    </Card>

                    <Card sx={{ 
                        minHeight: 1000, minWidth: 200, padding: 4, flex: 1, 
                        display: "flex", flexDirection: "column", 
                        marginLeft: 40 }}>
                        <CardContent>
                        
                        </CardContent>

                    </Card>

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