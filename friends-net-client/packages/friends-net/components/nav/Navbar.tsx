import { useCookies } from "react-cookie";
import AuthNavbar from "./AuthNavbar";
import UnauthNavbar from "./UnauthNavbar";

const Navbar: React.FC<{}> = () => {

    const [cookie] = useCookies(['accessToken']);

    return cookie.accessToken ? (
        // TODO useSnackbar here instead of in AuthNavbar
        <AuthNavbar />
    ) : <UnauthNavbar />
}

export default Navbar;