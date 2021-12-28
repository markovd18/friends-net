import { AuthApi, UserRegistrationDataVO } from "@markovda/fn-api";
import { AlertColor } from "@mui/material";
import { NextPage } from "next";
import Head from "next/head";
import * as React from "react";
import { useState } from "react";
import Navbar from "../components/nav/Navbar";
import RegistrationForm from "../components/RegistrationForm";
import SimpleSnackbar from "../components/SimpleSnackbar";
import { useSnackbar } from "../hooks";

const RegisterPage: NextPage = () => {

    const [Snackbar, showSnackbar] = useSnackbar();

    const processError = React.useCallback((status: number) => {
        switch (status) {
            case 400:
                showSnackbar('Selected credentials are not available. Please try again.', 'warning');
                return;
            default:
                showSnackbar('Unknown error while registering occured. Please try again later.', 'error');
                return;
        }
    }, []);
    
    const handleSubmit = React.useCallback(async (data: UserRegistrationDataVO): Promise<boolean> => {
        try {
            const status = (await AuthApi.register(data)).status;
            if (status === 200) {
                showSnackbar('Successfully registered! You now may log in.', 'success');
                return true;
            }

            processError(status);
        } catch(e) {
            showSnackbar('Selected credentials are not available. Please try again.', 'warning');            
        }

        return false;
    }, []);

    return (
        <>
            <Head>
                <title>Register to Friends Net</title>
                <meta name="description" content="Friends Net registration page"/>
            </Head>

            <main className="main">  
                <Navbar />
                <RegistrationForm onSubmit={handleSubmit} />  
                {Snackbar}
            </main>
            
        </>
    )
}

export default RegisterPage;