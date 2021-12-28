import { AlertColor, Button } from "@mui/material";
import { useSnackbar } from "packages/friends-net/hooks";
import { useCallback, useState } from "react";
import { useCookies } from "react-cookie";
import SimpleNavbar from "./SimpleNavbar";



const AuthNavbar: React.FC<{}> = () => {
    
    const [,, removeCookie] = useCookies(['accessToken']);
    const [Snackbar, showSnackbar] = useSnackbar();   

    const handleLogout = useCallback(() => {
        removeCookie('accessToken');
        showSnackbar('Succesfully logged out!', 'success');
    }, []);

    return (
        <>
            <SimpleNavbar>
                <Button
                        key='logout'
                        sx={{ my: 2, color: 'white', display: 'block' }}
                        onClick={handleLogout}
                    >
                        Logout
                    </Button>
            </SimpleNavbar>
            {Snackbar}
        </>
    )
}

export default AuthNavbar;