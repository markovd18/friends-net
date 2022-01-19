import { useCallback } from "react";
import { useCookies } from "react-cookie";
import { CookieSetOptions } from "universal-cookie";
import { UserData } from "../utils/authUtils";

const useUserData = (): [
    userData: UserData,
    loginUser: (userData: UserData) => void, 
    logoutUser: () => void
] => {
    
    const [cookies, setCookie, removeCookie] = useCookies(['accessToken', 'login', 'name', 'roles']);
    const cookieScope: CookieSetOptions = { path: '/'};

    const loginUser = useCallback((userData: UserData) => {
        setCookie("accessToken", userData.accessToken, cookieScope);
        setCookie("login", userData.login, cookieScope)
        setCookie("name", userData.name, cookieScope);
        setCookie("roles", userData.roles, cookieScope);
    }, []);

    const logoutUser = useCallback(() => {
        removeCookie("accessToken", cookieScope);
        removeCookie("login", cookieScope);
        removeCookie("name", cookieScope);
        removeCookie("roles", cookieScope);
        
    }, []);

    return [{
        accessToken: cookies.accessToken, 
        login: cookies.login, 
        name: cookies.name, 
        roles: cookies.roles
    }, loginUser, logoutUser];
}

export default useUserData;