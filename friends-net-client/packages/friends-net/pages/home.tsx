import { UserIdentificationDataVO } from "@markovda/fn-api";
import { Avatar, Card, CardContent, List, ListItem, ListItemButton, ListItemText, Stack, Typography } from "@mui/material";
import { Client, IMessage } from "@stomp/stompjs";
import { NextPage } from "next";
import Head from "next/head";
import { useEffect, useState } from "react";
import FriendshipsPageTab from "../components/FriendshipsPageTab";
import OnlineUserCard from "../components/home-page/OnlineUserCard";
import Navbar from "../components/nav/Navbar";
import PageContentContainer from "../components/PageContentContainer";
import { useAuthHeader } from "../hooks";
import useUnauthRedirect from "../hooks/useUnauthRedirect";
import useUserData from "../hooks/useUserData";
import { FriendStatus } from "../utils/enums/FriendStatus";
import { IFriendStatusChangeMessage } from "../utils/messaging/FriendStatusChangeMessage";

const HomePage: NextPage = () => {

    const [name, login] = useUserData();
    const [onlineUsers, setOnlineUsers] = useState<UserIdentificationDataVO[]>([]);
    const redirecting = useUnauthRedirect('/login');
    const authHeader = useAuthHeader();

    const convertStatusMessageToObject = (message: IMessage): IFriendStatusChangeMessage[] => {
        return JSON.parse(message.body);
    }

    const processFriendStatusChangeMessage = (message: IMessage) => {
        console.log("message:", message.body);

        const statusList = convertStatusMessageToObject(message);
        let wentOnline: UserIdentificationDataVO[] = [];
        let wentOffline: UserIdentificationDataVO[] = [];
        statusList.forEach(friend => friend.status === FriendStatus.ONLINE ? 
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

    useEffect(() => {
        if (redirecting || !authHeader) {
            return () => {};
        }

        const onConnect = () => {
            client.subscribe('/user/queue/friend-status', processFriendStatusChangeMessage, { 'Authorization': authHeader.headers.Authorization});
        }

        let client = new Client({
            brokerURL: 'ws://localhost:8080/messaging/status-change',
            connectHeaders: { 'Authorization': authHeader.headers.Authorization},
            reconnectDelay: 5000, 
            onConnect: onConnect,
            onDisconnect: () => console.log("disconnected"),
            beforeConnect: () => { }
        });


        client.activate();
        return () => client.deactivate();
    }, [redirecting]);
    
    return redirecting ? null : (
        <>
            <Head>
                <title>Friends Net</title>
                <meta name='description' content="Friends Net user's wallboard page"/>
            </Head>

            <main>
                <Navbar />
                <PageContentContainer>
                    <Card sx={{ maxWidth: 345, 
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
                            <Typography variant="body2">
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

                    <List
                        
                    >
                        {onlineUsers.map(user => (
                            <ListItem component="div" disablePadding key={user.login}>
                                <ListItemButton>
                                    <ListItemText primary={user.name}/>
                                </ListItemButton>
                            </ListItem>
                        ))}
                    </List>
                </PageContentContainer>
            </main>
        </>
    )
}

export default HomePage;