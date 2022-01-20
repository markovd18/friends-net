import * as React from 'react'
import { Button } from '@mui/material'
import SimpleNavbar from './SimpleNavbar';
import { useRouter } from 'next/router';


const UnauthNavbar: React.FC<{}> = () => {
    const router = useRouter();
    
    return (
        <SimpleNavbar >
            <Button
                key='Login'
                sx={{ my: 2, color: 'white', display: 'block' }}
                onClick={() => router.push('/login')}
            >
                Login
            </Button>
            <Button
                key='Register'
                sx={{ my: 2, color: 'white', display: 'block' }}
                onClick={() => router.push('/register')}
            >
                Register
            </Button>
        </SimpleNavbar>
    )
};

export default UnauthNavbar;