import { AuthApi, JwtVO, UserCredentialsVO } from '@markovda/fn-api';
import { AlertColor } from '@mui/material';
import { NextPage } from 'next'
import Head from 'next/head';
import Router from 'next/router';
import * as React from 'react'
import { useCookies } from 'react-cookie';
import LoginForm from '../components/LoginForm';
import UnauthorizedNavbar from '../components/nav/UnauthorizedNavbar';
import SimpleSnackbar from '../components/SimpleSnackbar';
import styles from '../styles/Home.module.css'

const LoginPage: NextPage = () => {

    const [snackbarOpen, setSnackbarOpen] = React.useState(false);
    const [snackbarMessage, setSnackbarMessage] = React.useState("");
    const [snackbarSeverity, setSnackbarSeverity] = React.useState('success');

    const [cookies, setCookie] = useCookies(['accessToken']);
    if (cookies.accessToken) {
        Router.push('/home');
    }

    const handleSnackbarClose = React.useCallback(() => {
        setSnackbarOpen(false);
    }, []);

    const showSnackbar = React.useCallback((message: string, severity: AlertColor) => {
        setSnackbarMessage(message);
        setSnackbarSeverity(severity);
        setSnackbarOpen(true);
    }, []);

    const authenticate = React.useCallback(async (data: UserCredentialsVO) => {
        const { token } = (await AuthApi.login(data)).data;
        setCookie('accessToken', token, { path: '/' });
    }, []);

    const handleSumbit = React.useCallback( async (data: UserCredentialsVO) => {
        try {
            authenticate(data);
        } catch (e) {
            showSnackbar('E-mail or password are incorrect', 'warning');
        }
    }, []);

    return cookies.accessToken ? null : (
        <>
            <Head>
                <title>Sign in to Friends Net</title>
                <meta name="description" content="Friends Net login page"/>
            </Head>
            <main className={styles.main}>
                <UnauthorizedNavbar />
                <LoginForm onSubmit={handleSumbit} />
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

export default LoginPage;