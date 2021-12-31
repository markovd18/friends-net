import { Avatar, Card, CardContent, Container, Stack, Typography } from "@mui/material";
import { NextPage } from "next";
import Head from "next/head";
import Navbar from "../components/nav/Navbar";
import PageContentContainer from "../components/PageContentContainer";
import useUnauthRedirect from "../hooks/useUnauthRedirect";
import useUserData from "../hooks/useUserData";

const HomePage: NextPage = () => {

    const [name, login] = useUserData();
    const redirecting = useUnauthRedirect('/login');

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
                        minHeight: 100, padding: 4, flex: 1, 
                        display: "flex", flexDirection: "column", 
                        justifyContent: "center", alignContent: "center",
                        position: "fixed" }}>
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
                        minHeight: 1000, padding: 4, flex: 1, 
                        display: "flex", flexDirection: "column", 
                        marginLeft: 50 }}>
                        <CardContent>
                        
                        </CardContent>

                    </Card>
                </PageContentContainer>
            </main>
        </>
    )
}

export default HomePage;