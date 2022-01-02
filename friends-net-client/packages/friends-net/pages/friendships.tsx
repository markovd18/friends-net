import { FriendshipApi, UserRelationshipVO, UserSearchApi } from "@markovda/fn-api";
import { Grid, Typography } from "@mui/material";
import { NextPage } from "next";
import Head from "next/head";
import { useCallback, useEffect, useState } from "react";
import FriendshipsPageTabCard from "../components/FriendshipsPageTabCard";
import Navbar from "../components/nav/Navbar";
import PageContentContainer from "../components/PageContentContainer";
import { useAuthHeader, useSnackbar, useUnauthRedirect, useUserData } from "../hooks";
import { FriendshipsPageTab } from "../utils/enums/RelationshipStatus";
import UserCardGrid from "../components/friendships-page/UserCardGrid";

const FriendshipsPage : NextPage = () => {

    const [activeTab, setActiveTab] = useState<FriendshipsPageTab>(FriendshipsPageTab.FRIENDS);
    const [showingSearchResults, setShowingSearchResults] = useState(false);
    const [lastSearchString, setLastSearchString] = useState<string>();
    const [lastSearchResult, setLastSearchResult] = useState<UserRelationshipVO[]>([]);

    const redirecting = useUnauthRedirect('/');
    const [,login,,logoutUser] = useUserData();
    const authHeader = useAuthHeader();
    const [Snackbar, showSnackbar] = useSnackbar();

    useEffect(() => {
        UserSearchApi.findFriends(authHeader).then(response => {
            setLastSearchResult(response.data);
        }).catch(error => {
            handleSearchError(error);
        });
    }, []);

    const changeActiveTab = useCallback(async (newActiveTab: FriendshipsPageTab) => {
        try {
            await switchActiveTab(newActiveTab);
        } catch (error) {
            handleSearchError(error);
        }
    }, []);

    const switchActiveTab = useCallback(async (newActiveTab: FriendshipsPageTab) => {
        if (newActiveTab === activeTab) {
            return;
        }

        switch (newActiveTab) {
            case FriendshipsPageTab.BLOCKED:
                setLastSearchResult((await UserSearchApi.findBlockedUsers(authHeader)).data);
                break;
            case FriendshipsPageTab.FRIENDS:
                setLastSearchResult((await UserSearchApi.findFriends(authHeader)).data);
                break;
            case FriendshipsPageTab.FRIEND_REQUESTS:
                setLastSearchResult((await UserSearchApi.findFriendRequests(authHeader)).data);
                break;
        }

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
                            {showingSearchResults &&
                                <>
                                    <Typography variant="h4">
                                        Search results for "{lastSearchString}":
                                    </Typography>
                                </>
                            }

                            <UserCardGrid 
                                data={lastSearchResult}
                                loggedInUsername={login}
                                pageTab={activeTab}
                                onSendFriendRequest={sendFriendRequest}
                            />
                        </Grid>
                    </Grid>
                    {Snackbar}
                </PageContentContainer>
            </main>
        </>
    )
}

export default FriendshipsPage;