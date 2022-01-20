import { EnumUserRole } from "@markovda/fn-api";
import { useRouter } from "next/router";
import { useEffect } from "react";
import { useCookies } from "react-cookie";
import { useUserData } from ".";
import { hasUserRole } from "../utils/authUtils";

/**
 * Performs redirect when user is unauthenticated.
 * @param path path where to redirect
 * @returns redirecting?
 */
const useUnauthRedirect = (path: string, role?: EnumUserRole): boolean => {

    const [cookies] = useCookies(['accessToken']);
    const [userData] = useUserData();
    const router = useRouter();

    useEffect(() => {
        if (!userData.accessToken || (role && !hasUserRole(userData, role))) {
            router.push(path);
        }
    }, [cookies]);

    return cookies.accessToken ? false : true;    
}

export default useUnauthRedirect;