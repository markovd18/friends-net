import { useRouter } from "next/router";
import { useEffect } from "react";
import { useCookies } from "react-cookie";

/**
 * Performs redirect when user is authenticated.
 * @param path path where to redirect
 * @returns redirecting?
 */
const useAuthRedirect = (path: string): boolean => {

    const [cookies] = useCookies(['accessToken']);
    const router = useRouter();
    
    useEffect(() => {
        if (cookies.accessToken) {
            router.push(path);
        }
    }, [cookies]);
    
    return cookies.accessToken ? true : false;    
}

export default useAuthRedirect;