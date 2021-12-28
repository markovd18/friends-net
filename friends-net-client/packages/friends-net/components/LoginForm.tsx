import { UserCredentialsVO, UserRegistrationDataVO } from '@markovda/fn-api'
import { Button, Link, Stack } from '@mui/material'
import * as React from 'react'
import FormInput from './FormInput'

type FormData = UserCredentialsVO;
type Props = {
    onSubmit: (data: FormData) => void,
}

const LoginForm: React.FC<Props> = ({onSubmit}) => {

    const [email, setEmail] = React.useState("");
    const [password, setPassword] = React.useState("")

    const updateEmail = React.useCallback((event: React.ChangeEvent<HTMLTextAreaElement>) => {
        event.preventDefault();
        setEmail(event.target.value);
    }, [email]);

    const updatePassword = React.useCallback((event: React.ChangeEvent<HTMLTextAreaElement>) => {
        event.preventDefault();
        setPassword(event.target.value);
    }, [password]);

    const handleSubmit = React.useCallback((event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        onSubmit({login: email, password: password});
    }, [email, password])

    return (
        <form onSubmit={handleSubmit} >
            <Stack spacing={1}>
                <FormInput 
                    id="email-input"
                    label="Email"
                    variant="standard"
                    type="email"
                    value={email}
                    onChange={updateEmail}
                    required
                />
                <FormInput 
                    id="password-input"
                    label="Password"
                    variant="standard"
                    type="password"
                    value={password}
                    onChange={updatePassword}
                    required
                />
                <Button variant="contained" type="submit">
                    Sign In
                </Button>    
                <Link href='/register' underline='hover'>
                    Don't have an account yet? Create one now.
                </Link>
            </Stack>
        </form>
    )
}

export default LoginForm;