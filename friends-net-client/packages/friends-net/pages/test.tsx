
import type { NextPage } from 'next'
import Button from '@mui/material/Button'
import { AuthApi, UserSearchApi } from '@markovda/fn-api'
import { useCookies } from 'react-cookie'
import { useRouter } from 'next/router'

const TestPage : NextPage = () => {

    const [, setCookie, removeCookie] = useCookies(['accessToken']);
    const router = useRouter();

    const onButtonClicked = async () => {
        // const status = (await AuthApi.register({login: "test-react", password: "heslo-react"})).status;
        // console.log("status", status);

        const token = (await AuthApi.login({login: "admin", password: "Strong_password"})).data;
        console.log("token", token);
        setCookie('accessToken', token.token, { path: '/' });
        router.push('/');
    }

    const deleteToken = () => {
        removeCookie('accessToken', { path: '/'})
    }

    const searchForUsers = async () => {
        const users = (await UserSearchApi.findUsers("makarek")).data;
        console.log("found:", users);
    }

    return (
        <div>
            <Button variant='contained' onClick={onButtonClicked}>Hello, Material UI!</Button>
            <Button onClick={deleteToken}>Remove token</Button>

            <Button variant='outlined' onClick={searchForUsers}></Button>
        </div>
    )
}

export default TestPage;