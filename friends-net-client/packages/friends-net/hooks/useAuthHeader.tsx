import { useCookies } from "react-cookie";

const useAuthHeader = () => {

    const [cookie] = useCookies(['accessToken'])
    if (!cookie.accessToken) {
        return undefined;
    }

    return { headers: { Authorization: "Bearer " + cookie.accessToken }}
}

export default useAuthHeader;