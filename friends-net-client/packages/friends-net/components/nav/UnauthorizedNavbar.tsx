import * as React from 'react'
import { AppBar, Box, Button, Container, Toolbar, Typography } from '@mui/material'
import Navbar from './Navbar';

const pages = ['Login', 'Register'];

const UnauthorizedNavbar: React.FC<{}> = () => {

    return (
        <Navbar >
            {pages.map((page) => (
                <Button
                    key={page}
                    sx={{ my: 2, color: 'white', display: 'block' }}
                >
                    {page}
                </Button>
            ))}
        </Navbar>
    )
};

export default UnauthorizedNavbar;