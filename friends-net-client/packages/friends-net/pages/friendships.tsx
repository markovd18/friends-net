import { FriendshipApi, UserIdentificationDataVO, UserSearchApi } from "@markovda/fn-api";
import { Avatar, Box, Button, Card, CardActions, CardContent, CardMedia, Grid, Stack, Typography } from "@mui/material";
import { NextPage } from "next";
import Head from "next/head";
import { useCallback, useState } from "react";
import FriendshipsPageTabCard from "../components/FriendshipsPageTabCard";
import Navbar from "../components/nav/Navbar";
import PageContentContainer from "../components/PageContentContainer";
import { useAuthHeader, useSnackbar, useUnauthRedirect, useUserData } from "../hooks";
import { FriendshipsPageTab } from "../utils/enums/RelationshipStatus";

const FriendshipsPage : NextPage = () => {

    const tmpRequests = [
        {key: "DM", name: "David Markov", login: "makarek@pia.cz"},
        {key: "MB", name: "Martin Brožek", login: "frostyx@nerd.cz"},
        {key: "JH", name: "Jaroslav Hrubý", login: "ani-picu@nestihne.cz"},
        {key: "ND", name: "Nejlepsi drepar", login: "na-svete@vy-mrdky.cz"},
        {key: "VT", name: "Vaříme s tátou", login: "dve-zmacknuti@uplne.cz"}
    ];

    const tmpSearchResults = [
        {key: "A", name: "Makarek", login: "makarek@pia.cz"},
        {key: "B", name: "Periphery", login: "alpha-omega@p3.com"}
    ]

    const [activeTab, setActiveTab] = useState(FriendshipsPageTab.FRIENDS);
    const [showingSearchResults, setShowingSearchResults] = useState(false);
    const [lastSearchString, setLastSearchString] = useState<string>();
    const [lastSearchResult, setLastSearchResult] = useState<UserIdentificationDataVO[]>([]);

    const redirecting = useUnauthRedirect('/');
    const [,login,,logoutUser] = useUserData();
    const authHeader = useAuthHeader();
    const [Snackbar, showSnackbar] = useSnackbar();

    const changeActiveTab = useCallback((newActiveTab: FriendshipsPageTab) => {
        setActiveTab(newActiveTab);
        setShowingSearchResults(false);
    }, []);

    const handleSearchError = useCallback((error) => {
        if (!error.response) {
            showSnackbar('Server not responding. Please try again later.', 'error');
            return;
        }

        switch (error.response.status) {
            case 401:
                logoutUser();
                break;
            default:
                showSnackbar('Unknown error occured. Please try again later.', 'error');
        }
    }, []);

    const handleFriendRequestError = useCallback(error => {
        if (!error.response) {
            showSnackbar('Server not responding. Please try again later.', 'error');
            return;
        }

        switch (error.response.status) {
            case 400:
                showSnackbar('Invalid user for sending friend request', 'warning');
                break;
            case 401:
                logoutUser();
                break;
            default:
                showSnackbar('Unknown error occured. Please try again later.', 'error');
        }
    }, []);

    const handleUserSearch = useCallback(async (searchString: string) => {
        try {
            const foundUsers = (await UserSearchApi.findUsers(searchString, authHeader)).data;
            setShowingSearchResults(true);
            setLastSearchResult(foundUsers);
            setLastSearchString(searchString);
        } catch (error) {
            handleSearchError(error);
        }
    }, []);

    const sendFriendRequest = useCallback(async (login: string) => {
        try {
            await FriendshipApi.createFriendRequest(login, authHeader);
            setShowingSearchResults(false);
        } catch (error) {
            handleFriendRequestError(error);
        }
    }, []);

    return redirecting ? null : (
        <>
            <Head>
                <title>Friends Net - Friendships</title>
                <meta name="description" content="Friends Net's sriendships page"/>
            </Head>

            <main>
                <Navbar />
                <PageContentContainer>
                    <Grid container columnSpacing={5}>
                        <Grid item xs={12} sm={3}>
                            <FriendshipsPageTabCard 
                                activeTab={activeTab}
                                onClick={changeActiveTab} 
                                onSearchSubmit={handleUserSearch}   
                            />
                        </Grid>
                        <Grid item xs={12} sm={9}>
                            {showingSearchResults 
                            ?
                                <>
                                    <Typography variant="h4">
                                        Search results for "{lastSearchString}":
                                    </Typography>
                                    <Grid container spacing={2} marginTop={1}>
                                        {lastSearchResult.map(foundUser => (
                                            <Grid item xs={3} key={foundUser.login}>
                                                <Card elevation={2}>
                                                    <CardMedia sx={{alignItems: 'center', justifyContent: 'center', display: 'flex'}}>
                                                        <Box paddingTop={2}>
                                                            <Avatar>
                                                                {foundUser.name.charAt(0)}
                                                            </Avatar>
                                                        </Box>
                                                    </CardMedia>
                                                    <CardContent>
                                                        <Typography variant="h5">
                                                            {foundUser.name}
                                                        </Typography>
                                                        <Typography variant="body2" color={'text.secondary'}>
                                                            {foundUser.login}
                                                        </Typography>
                                                    </CardContent>
                                                    <CardActions sx={{alignItems: 'center', justifyContent: 'center', display: 'flex'}}>
                                                        <Stack spacing={1}>
                                                            {foundUser.login === login 
                                                            ? <Button variant="outlined" disabled>Me</Button>
                                                            : <Button variant="contained" onClick={() => sendFriendRequest(foundUser.login)}>Send friend request</Button>}
                                                        </Stack>
                                                    </CardActions>
                                                </Card>
                                            </Grid>
                                        ))}
                                    </Grid>
                                </>
                            :

                            <Grid container spacing={2}>
                                {tmpRequests.map(request => (
                                    
                                    <Grid item xs={3} key={request.login}>
                                        <Card elevation={2}>
                                            <CardMedia sx={{alignItems: 'center', justifyContent: 'center', display: 'flex'}}>
                                                <Box paddingTop={2}>
                                                    <Avatar>
                                                        {request.name.charAt(0)}
                                                    </Avatar>
                                                </Box>
                                            </CardMedia>
                                            <CardContent>
                                                <Typography variant="h5">
                                                    {request.name}
                                                </Typography>
                                                <Typography variant="body2" color={'text.secondary'}>
                                                    {request.login}
                                                </Typography>
                                            </CardContent>
                                            <CardActions sx={{alignItems: 'center', justifyContent: 'center', display: 'flex'}}>
                                                <Stack spacing={1}>
                                                    <Button variant="contained">Accept request</Button>
                                                    <Button variant="outlined">Delete request</Button>
                                                    <Button variant="contained" color="error">Block user</Button>
                                                </Stack>
                                            </CardActions>
                                        </Card>
                                    </Grid>
                                ))}
                                
                            </Grid>
                            }
                        </Grid>
                    </Grid>
                    {Snackbar}
                </PageContentContainer>
            </main>
        </>
    )
}

export default FriendshipsPage;