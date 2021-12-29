import { AlertColor, Button } from "@mui/material";
import { useSnackbar } from "packages/friends-net/hooks";
import { useCallback, useState } from "react";
import { useCookies } from "react-cookie";
import SimpleNavbar from "./SimpleNavbar";


type Props = {
    onLogout: () => void;
}

const AuthNavbar: React.FC<Props> = ({onLogout}) => {   

    return (
        <SimpleNavbar>
            <Button
                    key='logout'
                    sx={{ my: 2, color: 'white', display: 'block' }}
                    onClick={onLogout}
                >
                    Logout
            </Button>
        </SimpleNavbar>
    )
}

export default AuthNavbar;