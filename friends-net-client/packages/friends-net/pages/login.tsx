import { AuthApi, UserCredentialsVO } from '@markovda/fn-api';
import { NextPage } from 'next'
import Head from 'next/head';
import { useCallback } from 'react';
import LoginForm from '../components/LoginForm';
import Navbar from '../components/nav/Navbar';
import { useAuthRedirect, useSnackbar } from '../hooks';
import useUserData from '../hooks/useUserData';
import styles from '../styles/Home.module.css'

const LoginPage: NextPage = () => {

    const [,loginUser] = useUserData();
    const redirecting = useAuthRedirect('/home');
    const [snackbar, showSnackbar] = useSnackbar();

    const authenticate = useCallback(async (data: UserCredentialsVO) => {
        const { data: { login, name, token, roles } } = (await AuthApi.login(data));
        loginUser({login: login, name: name, accessToken: token, roles: roles});
    }, []);

    const handleError = useCallback((error) => {
        if (!error.response) {
            showSnackbar('Server not responding. Please try again later.', 'error');
            return;
        }

        switch (error.response.status){
            case 400:
            case 401:
            case 403:
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
                <Navbar />
                <LoginForm onSubmit={handleSumbit} />
                {snackbar}
            </main>
        </>
    )
}

export default LoginPage;