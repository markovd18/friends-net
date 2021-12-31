import { useCallback } from "react";
import { useCookies } from "react-cookie";
import { CookieSetOptions } from "universal-cookie";

type UserData = {
    name: string, 
    login: string,
    accessToken: string
}

type UserContextData = {
    userData: UserData,
    loginUser: (userData: UserData) => void,
    logoutUser: () => void
}

const useUserData = (): [
    name: string, 
    login: string, 
    loginUser: (userData: UserData) => void, 
    logoutUser: () => void
] => {
    
    const [cookies, setCookie, removeCookie] = useCookies(['accessToken', 'login', 'name']);
    const cookieScope: CookieSetOptions = { path: '/'};

    const loginUser = useCallback((userData: UserData) => {

        setCookie("accessToken", userData.accessToken, cookieScope);
        setCookie("login", userData.login, cookieScope)
        setCookie("name", userData.name, cookieScope);
    }, []);

    const logoutUser = useCallback(() => {
        removeCookie("accessToken", cookieScope);
        removeCookie("login", cookieScope);
        removeCookie("name", cookieScope);
        
    }, []);

    return [cookies.name, cookies.login, loginUser, logoutUser];
}

export default useUserData;