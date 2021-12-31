import { AuthApi, UserCredentialsVO } from '@markovda/fn-api';
import { AlertColor } from '@mui/material';
import { NextPage } from 'next'
import Head from 'next/head';
import { useCallback, useState } from 'react';
import { useCookies } from 'react-cookie';
import LoginForm from '../components/LoginForm';
import UnauthNavbar from '../components/nav/UnauthNavbar';
import SimpleSnackbar from '../components/SimpleSnackbar';
import { useAuthRedirect } from '../hooks';
import useUserData from '../hooks/useUserData';
import styles from '../styles/Home.module.css'

const LoginPage: NextPage = () => {

    const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
    const [snackbarMessage, setSnackbarMessage] = useState<string>("");
    const [snackbarSeverity, setSnackbarSeverity] = useState<AlertColor>('success');

    const [,,loginUser,] = useUserData();
    const redirecting = useAuthRedirect('/home');

    const handleSnackbarClose = useCallback(() => {
        setSnackbarOpen(false);
    }, []);

    const showSnackbar = useCallback((message: string, severity: AlertColor) => {
        setSnackbarMessage(message);
        setSnackbarSeverity(severity);
        setSnackbarOpen(true);
    }, []);

    const authenticate = useCallback(async (data: UserCredentialsVO) => {
        const { data: { login, name, token } } = (await AuthApi.login(data));
        loginUser({login: login, name: name, accessToken: token});
    }, []);

    const handleError = useCallback((error) => {
        if (!error.response) {
            showSnackbar('Server not responding. Please try again later.', 'error');
            return;
        }

        switch (error.response.status){
            case 400:
            case 401:
                showSnackbar('E-mail or password are incorrect', 'warning');
                break;
            case 404:
                showSnackbar('Error while accessing server. Please try again alter.', 'error');
                break;
            default:
                showSnackbar('Unknown error occured. Please try again later.', 'error');
        }
    }, []);

    const handleSumbit = useCallback( async (data: UserCredentialsVO) => {
        try {
            await authenticate(data);
        } catch (e) {
            handleError(e);
        }
    }, []);

    return redirecting ? null : (
        <>
            <Head>
                <title>Sign in to Friends Net</title>
                <meta name="description" content="Friends Net login page"/>
            </Head>
            <main className={styles.main}>
                <UnauthNavbar />
                <LoginForm onSubmit={handleSumbit} />
                <SimpleSnackbar 
                    open={snackbarOpen} 
                    message={snackbarMessage} 
                    severity={snackbarSeverity} 
                    handleClose={handleSnackbarClose}
                />
            </main>
        </>
    )
}

export default LoginPage;