import { Avatar, Box, Button, Card, CardActionArea, CardActions, CardContent, CardMedia, Grid, Input, Stack, TextField, Typography } from "@mui/material";
import { NextPage } from "next";
import Head from "next/head";
import Navbar from "../components/nav/Navbar";
import PageContentContainer from "../components/PageContentContainer";
import useUnauthRedirect from "../hooks/useUnauthRedirect";

const FriendshipsPage : NextPage = () => {

    const tmpRequests = [
        {name: "David Markov", login: "makarek@pia.cz"},
        {name: "Martin Brožek", login: "frostyx@nerd.cz"},
        {name: "Jaroslav Hrubý", login: "ani-picu@nestihne.cz"},
        {name: "Nejlepsi drepar", login: "na-svete@vy-mrdky.cz"},
        {name: "Vaříme s tátou", login: "dve-zmacknuti@uplne.cz"}
    ]

    const redirecting = useUnauthRedirect('/');
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
                            <Card>
                                <Stack spacing={0} padding={1}>
                                    <Card elevation={0}>
                                        <CardActionArea onClick={() => console.log("clicked")}>
                                            <CardContent>
                                                <Typography variant="h5" color='text.secondary'>
                                                    Friends
                                                </Typography>
                                            </CardContent>
                                        </CardActionArea>
                                    </Card>
                                    <Card elevation={0}>
                                        <CardActionArea onClick={() => console.log("clicked")}>
                                            <CardContent>
                                                <Typography variant="h5" color='text.secondary'>
                                                    Friend requests
                                                </Typography>
                                            </CardContent>
                                        </CardActionArea>
                                    </Card>
                                    <Card elevation={0}>
                                        <CardActionArea onClick={() => console.log("clicked")}>
                                            <CardContent>
                                                <Typography variant="h5" color='text.secondary'>
                                                    Blocked users
                                                </Typography>
                                            </CardContent>
                                        </CardActionArea>
                                    </Card>
                                </Stack>

                                
                                    <TextField variant="filled" color="success" size="small" fullWidth placeholder="Find new friends"/>
                                
                            </Card>

                        </Grid>

                    <Grid item xs={12} sm={9}>
                        <Grid container spacing={2}>
                            {tmpRequests.map(request => (
                                
                                <Grid item xs={3}>
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
                    </Grid>
                    </Grid>
                </PageContentContainer>
            </main>
        </>
    )
}

export default FriendshipsPage;