import { AuthApi, UserRegistrationDataVO } from "@markovda/fn-api";
import { AlertColor } from "@mui/material";
import { NextPage } from "next";
import Head from "next/head";
import * as React from "react";
import { useState } from "react";
import UnauthorizedNavbar from "../components/nav/UnauthorizedNavbar";
import RegistrationForm from "../components/RegistrationForm";
import SimpleSnackbar from "../components/SimpleSnackbar";

const RegisterPage: NextPage = () => {

    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState("");
    const [snackbarSeverity, setSnackbarSeverity] = useState('success');

    const handleSnackbarClose = React.useCallback(() => {
        setSnackbarOpen(false);
    }, []);

    const showSnackbar = React.useCallback((message: string, severity: AlertColor) => {
        setSnackbarMessage(message);
        setSnackbarSeverity(severity);
        setSnackbarOpen(true);
    }, []);

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
                <UnauthorizedNavbar />
                <RegistrationForm onSubmit={handleSubmit} />  
                <SimpleSnackbar 
                    open={snackbarOpen} 
                    message={snackbarMessage} 
                    severity={snackbarSeverity as AlertColor} 
                    handleClose={handleSnackbarClose}
                />
            </main>
            
        </>
    )
}

export default RegisterPage;