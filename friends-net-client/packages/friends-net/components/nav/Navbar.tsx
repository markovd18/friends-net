import useUserData from "packages/friends-net/hooks/useUserData";
import { hasAdminRole } from "packages/friends-net/utils/authUtils";
import { useCallback } from "react";
import { useCookies } from "react-cookie";
import AuthNavbar from "./AuthNavbar";
import UnauthNavbar from "./UnauthNavbar";

const Navbar: React.FC<{}> = () => {

    const [cookie] = useCookies(['accessToken']);   
    const [,,logoutUser] = useUserData();

    const handleLogout = useCallback(() => {
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