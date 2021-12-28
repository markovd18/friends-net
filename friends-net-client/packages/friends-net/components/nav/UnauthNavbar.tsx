import * as React from 'react'
import { AppBar, Box, Button, Container, Toolbar, Typography } from '@mui/material'
import SimpleNavbar from './SimpleNavbar';

const pages = ['Login', 'Register'];

const UnauthNavbar: React.FC<{}> = () => {

    return (
        <SimpleNavbar >
            {pages.map((page) => (
                <Button
                    key={page}
                    sx={{ my: 2, color: 'white', display: 'block' }}
                >
                    {page}
                </Button>
            ))}
        </SimpleNavbar>
    )
};

export default UnauthNavbar;