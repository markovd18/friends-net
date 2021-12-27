import * as React from 'react'
import { AppBar, Box, Link, Container, Toolbar, Typography } from '@mui/material'


const Navbar: React.FC<{}> = ({children}) => {

    return (
        <AppBar>
            <Container maxWidth='lg'>
                <Toolbar disableGutters>
                
                    <Typography
                        variant='h6'
                        noWrap
                        component='div'
                        sx={{ mr: 2, display: { xs: 'none', md: 'flex' } }}
                    >
                    <Link underline='none' href='/' color={'white'}>Friends Net</Link>
                    </Typography>

                <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
                    {children}
                </Box>

                </Toolbar>
            </Container>
        </AppBar>
    )
};

export default Navbar;