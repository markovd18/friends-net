import { UserCredentialsVO } from '@markovda/fn-api'
import { Button, Link, Stack } from '@mui/material'
import { useCallback, useState } from 'react';
import FormInput from './FormInput'

type FormData = UserCredentialsVO;
type Props = {
    onSubmit: (data: FormData) => void,
}

const LoginForm: React.FC<Props> = ({onSubmit}) => {

    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("")

    const updateEmail = useCallback((event: React.ChangeEvent<HTMLTextAreaElement>) => {
        event.preventDefault();
        setEmail(event.target.value);
    }, [email]);

    const updatePassword = useCallback((event: React.ChangeEvent<HTMLTextAreaElement>) => {
        event.preventDefault();
        setPassword(event.target.value);
    }, [password]);

    const handleSubmit = useCallback((event: React.FormEvent<HTMLFormElement>) => {
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
                    Don&apos;t have an account yet? Create one now.
                </Link>
            </Stack>
        </form>
    )
}

export default LoginForm;