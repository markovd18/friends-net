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

const HomePage: NextPage = () => {

    const [chatHidden, setChatHidden] = useState<boolean>(true);
    const [chatWith, setChatWith] = useState<UserIdentificationDataVO | undefined>();
    const [name, login] = useUserData();
    const redirecting = useUnauthRedirect('/login');
    const [onlineUsers, sendMessage, messages] = useMessagingConnection(redirecting);

    const showChat = (userData: UserIdentificationDataVO) => {
        setChatWith(userData);
        setChatHidden(false);
    }

    const hideChat = () => {
        setChatHidden(true);
        setChatWith(undefined);
    }

    const handleMessageSubmit = (message: string) => {
        if (chatWith) {
            sendMessage({to: chatWith.login, content: message});
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
                        messages={messages}
                        onClose={hideChat}
                        onMessageSubmit={handleMessageSubmit}
                    />
                </PageContentContainer>
            </main>
        </>
    )
}

export default HomePage;