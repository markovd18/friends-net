import { AuthApi, UserRegistrationDataVO } from "@markovda/fn-api";
import { AlertColor } from "@mui/material";
import { NextPage } from "next";
import Head from "next/head";
import * as React from "react";
import { useCallback, useState } from "react";
import Navbar from "../components/nav/Navbar";
import RegistrationForm from "../components/RegistrationForm";
import SimpleSnackbar from "../components/SimpleSnackbar";
import { useSnackbar } from "../hooks";

const RegisterPage: NextPage = () => {

    const [Snackbar, showSnackbar] = useSnackbar();

    const processError = useCallback((error) => {
        if (!error.response) {
            showSnackbar('Server not responding. PLease try again later.', 'error');
            return;
        }

        switch (error.response.status) {
            case 400:
                showSnackbar('Selected email is not available. Please try again.', 'warning');
                return;
            case 404:
                showSnackbar('Server not responding. Please try again later.', 'error');
                return;
            default:
                showSnackbar('Unknown error while registering occured. Please try again later.', 'error');
                return;
        }
    }, []);
    
    const handleSubmit = useCallback(async (data: UserRegistrationDataVO): Promise<boolean> => {
        try {
            await AuthApi.register(data);
            showSnackbar('Successfully registered! You now may log in.', 'success');
            return true;
        } catch(error) {
            processError(error);
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