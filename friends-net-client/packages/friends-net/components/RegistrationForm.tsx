import { UserRegistrationDataVO } from '@markovda/fn-api'
import { Button, Link, Stack } from '@mui/material'
import * as React from 'react'
import { useCallback, useState } from 'react';
import FormInput from './FormInput'

type FormData = UserRegistrationDataVO;

type Props = {
    onSubmit: (data: FormData) => Promise<boolean>,
}

const RegistrationForm: React.FC<Props> = ({onSubmit}) => {

    const [email, setEmail] = useState<string>("");
    const [password, setPassword] = useState<string>("")
    const [name, setName] = useState<string>("");
    const [nameTooShort, setNameTooShort] = useState<boolean>(false);
    const [nameTooLong, setNameTooLong] = useState<boolean>(false);

    const updateEmail = useCallback((event: React.ChangeEvent<HTMLTextAreaElement>) => {
        event.preventDefault();
        setEmail(event.target.value);
    }, [email]);

    const updatePassword = useCallback((event: React.ChangeEvent<HTMLTextAreaElement>) => {
        event.preventDefault();
        setPassword(event.target.value);
    }, [password]);

    const updateName = useCallback((event: React.ChangeEvent<HTMLTextAreaElement>) => {
        event.preventDefault();
        setName(event.target.value);
    }, [name]);

    const clearForm = React.useCallback(() => {
        setEmail("");
        setPassword("");
        setName("");
        setNameTooShort(false);
        setNameTooLong(false);
    }, []);

    const handleSubmit = React.useCallback(async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (name.length < 3) {
            setNameTooShort(true);
            return;
        }

        if (name.length > 50) {
            setNameTooLong(true);
            return;
        }

        const success: boolean = await onSubmit({login: email, password: password, name: name});
        if (success)  {
            clearForm();
        }
    }, [email, password, name])

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
                    helperText="Email will be used as a login"
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
                <FormInput 
                    id="name-input"
                    label="Name"
                    variant="standard"
                    value={name}
                    onChange={updateName}
                    required
                    error={nameTooShort || nameTooLong}
                    errorText={nameTooShort ? 
                        'Name has to be at least 3 chatacters long' : 
                        'Name has to be max. 50 chatacters long'}
                />
                <Button variant="contained" type="submit">
                    Sign Up
                </Button>    
                <Button variant='outlined' type='button' onClick={clearForm}>
                    Clear
                </Button>
                <Link href='/login' underline='hover'>
                    Already have an account? Sign in.
                </Link>
            </Stack>
        </form>
    )
}

export default RegistrationForm;