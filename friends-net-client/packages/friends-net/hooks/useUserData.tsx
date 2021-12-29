
import { useContext } from "react";
import { UserContext, UserDataProvider } from "../contexts/UserContext";

const useUserData = () => {
    return useContext(UserContext);
}

export default useUserData;