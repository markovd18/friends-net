import { useSnackbar } from "packages/friends-net/hooks";
import useUserData from "packages/friends-net/hooks/useUserData";
import { useCallback } from "react";
import { useCookies } from "react-cookie";
import AuthNavbar from "./AuthNavbar";
import UnauthNavbar from "./UnauthNavbar";

const Navbar: React.FC<{}> = () => {

    const [cookie,, removeCookie] = useCookies(['accessToken']);   
    const { logoutUser } = useUserData();

    const handleLogout = useCallback(() => {
        removeCookie('accessToken', { path: '/' });
        logoutUser();
    }, []);

    return (
        <>
            {cookie.accessToken 
            ? <AuthNavbar onLogout={handleLogout}/> 
            : <UnauthNavbar />
            }
        </>
    )
}

export default Navbar;