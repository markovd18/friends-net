import { useRouter } from "next/router";
import { useEffect } from "react";
import { useCookies } from "react-cookie";

/**
 * Performs redirect when user is unauthenticated.
 * @param path path where to redirect
 * @returns redirecting?
 */
const useUnauthRedirect = (path: string): boolean => {

    const [cookies] = useCookies(['accessToken']);
    const router = useRouter();

    useEffect(() => {
        if (!cookies.accessToken) {
            router.push(path);
        }
    }, [cookies]);

    return cookies.accessToken ? false : true;    
}

export default useUnauthRedirect;