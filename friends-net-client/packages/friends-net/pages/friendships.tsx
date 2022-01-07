import { FriendshipApi, UserRelationshipVO, UserSearchApi } from "@markovda/fn-api";
import { CircularProgress, Grid, Typography } from "@mui/material";
import { NextPage } from "next";
import Head from "next/head";
import { useCallback, useEffect, useState } from "react";
import FriendshipsPageTabCard from "../components/FriendshipsPageTabCard";
import Navbar from "../components/nav/Navbar";
import PageContentContainer from "../components/PageContentContainer";
import { useAuthHeader, useSnackbar, useUnauthRedirect, useUserData } from "../hooks";
import { FriendshipsPageTab } from "../utils/enums/RelationshipStatus";
import UserCardGrid from "../components/friendships-page/UserCardGrid";
import { Box } from "@mui/system";

const FriendshipsPage : NextPage = () => {

    const [activeTab, setActiveTab] = useState<FriendshipsPageTab>(FriendshipsPageTab.FRIENDS);
    const [showingSearchResults, setShowingSearchResults] = useState(false);
    const [lastSearchString, setLastSearchString] = useState<string>();
    const [lastSearchResult, setLastSearchResult] = useState<UserRelationshipVO[]>([]);
    const [isDataLoading, setIsDataLoading] = useState(false);

    const redirecting = useUnauthRedirect('/');
    const [,login,,logoutUser] = useUserData();
    const authHeader = useAuthHeader();
    const [Snackbar, showSnackbar] = useSnackbar();

    useEffect(() => {
        setLastSearchResult([]);

        const fetchData = async () => {
            setIsDataLoading(true);
            let data: UserRelationshipVO[] = [];
            try {
                switch (activeTab) {
                    case FriendshipsPageTab.BLOCKED:
                        data = (await UserSearchApi.findBlockedUsers(authHeader)).data;
                        break;
                    case FriendshipsPageTab.FRIENDS:
                        data = (await UserSearchApi.findFriends(authHeader)).data;
                        break;
                    case FriendshipsPageTab.FRIEND_REQUESTS:
                        data = (await UserSearchApi.findFriendRequests(authHeader)).data;
                        break;
                }
                
                setLastSearchResult(data);
                setIsDataLoading(false);
            } catch (error) {
                handleSearchError(error);
            }
        }

        fetchData();
   }, [activeTab]);

    // useEffect(() => {
    //     UserSearchApi.findFriends(authHeader).then(response => {
    //         setLastSearchResult(response.data);
    //     }).catch(error => handleSearchError(error));
    // }, []);

    const changeActiveTab = useCallback(async (newActiveTab: FriendshipsPageTab) => {
        try {
            await switchActiveTab(newActiveTab);
        } catch (error) {
            handleSearchError(error);
        }
    }, []);

    const switchActiveTab = useCallback(async (newActiveTab: FriendshipsPageTab) => {
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

    const handleRelationshipChangeError = useCallback(error => {
        if (!error.response) {
            showSnackbar('Server not responding. Please try again later.', 'error');
            return;
        }

        switch (error.response.status) {
            case 400:
                showSnackbar('Target user not found', 'warning');
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
            setActiveTab(FriendshipsPageTab.SEARCH_RESULTS);
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
            await changeActiveTab(FriendshipsPageTab.FRIEND_REQUESTS);
        } catch (error) {
            handleRelationshipChangeError(error);
        }
    }, []);

    const removeUser = useCallback(async (login: string) => {
        try {
            await FriendshipApi.deleteRelationship(login, authHeader);
            await changeActiveTab(FriendshipsPageTab.FRIEND_REQUESTS);
        } catch (error) {
            handleRelationshipChangeError(error);
        }
    }, []);

    const acceptFriendRequest = useCallback(async (login: string) => {
        try {
            await FriendshipApi.acceptFriendRequest(login, authHeader);
            await changeActiveTab(FriendshipsPageTab.FRIEND_REQUESTS);
        } catch (error) {
            handleRelationshipChangeError(error);
        }
    }, []);

    const blockUser = useCallback(async (login: string) => {
        try {
            await FriendshipApi.blockUser(login, authHeader);
            await changeActiveTab(FriendshipsPageTab.FRIEND_REQUESTS);
        } catch(error) {
            handleRelationshipChangeError(error);
        }
    }, []);

    const unblockUser = useCallback(async (login: string) => {
        try {
            await FriendshipApi.unblockUser(login, authHeader);
            await changeActiveTab(FriendshipsPageTab.FRIEND_REQUESTS);
        } catch (error) {
            handleRelationshipChangeError(error);
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
                        <Grid item xs={12} sm={4}>
                            <FriendshipsPageTabCard 
                                activeTab={activeTab}
                                onClick={changeActiveTab} 
                                onSearchSubmit={handleUserSearch}   
                            />
                        </Grid>
                        <Grid item xs={12} sm={8}>
                            {isDataLoading ? (
                                <Box sx={{display: 'flex'}}>
                                    <CircularProgress />
                                </Box>
                            ) : (
                                <>
                                    {showingSearchResults && 
                                        <>
                                            <Typography variant="h4">
                                                Search results for "{lastSearchString}":
                                            </Typography>
                                        </>}
                                    
                                    <UserCardGrid 
                                        data={lastSearchResult}
                                        loggedInUsername={login}
                                        pageTab={activeTab}
                                        onSendFriendRequest={sendFriendRequest}
                                        onRemoveUser={removeUser}
                                        onAcceptRequest={acceptFriendRequest}
                                        onBlockUser={blockUser}
                                        onUnblockUser={unblockUser}
                                    />
                                </>
                            )}
                        </Grid>
                    </Grid>
                    {Snackbar}
                </PageContentContainer>
            </main>
        </>
    )
}

export default FriendshipsPage;