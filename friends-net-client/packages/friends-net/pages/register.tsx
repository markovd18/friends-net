import { Box, Button, FormControl, FormHelperText, Input, InputLabel } from "@mui/material";
import { NextPage } from "next";
import Head from "next/head";
import Router from "next/router";
import UnauthorizedNavbar from "../components/nav/UnauthorizedNavbar";

const RegisterPage: NextPage = () => {

    const handleSubmit = () => {
        console.log("Registering")
    }

    return (
        <>
            <Head>
                <title>Register to Friends Net</title>
                <meta name="description" content="Friends Net registration page"/>

            </Head>

            <main className="main">
                
                <UnauthorizedNavbar />
                <form onSubmit={handleSubmit} >
                    <FormControl variant="standard" required fullWidth>
                        <InputLabel htmlFor="email-input">Email</InputLabel>
                        <Input id="email-input" type="email" aria-describedby="email-desc"/>
                        <FormHelperText id="email-desc">Email will be used as a login</FormHelperText>
                    </FormControl>
                    <FormControl variant="standard" required fullWidth>
                        <InputLabel htmlFor="password-input">Password</InputLabel>
                        <Input id="password-input" type="password"></Input>
                    </FormControl>
                    <FormControl variant="standard" required fullWidth>
                        <InputLabel htmlFor="name-input">Name</InputLabel>
                        <Input id="name-input" aria-describedby="name-desc"></Input>
                        <FormHelperText id="name-desc">This is how others will see you.</FormHelperText>
                    </FormControl>
                    <Button variant="contained" type="submit">
                        Sign Up
                    </Button>
                </form>    
            </main>
            
        </>
    )
}

export default RegisterPage;