import { useCallback, useState } from "react";
import UserRolesDialog, { UserData } from "../components/UserRolesDialog";



const useUserRolesDialog = (
    onAdminToggled?:(login: string, isAdmin: boolean) => void,
    ):[
        JSX.Element, 
        (data: UserData[]) => void,
    ] => {

    const [isOpen, setIsOpen] = useState<boolean>(false);
    const [data, setData] = useState<UserData[]>([]);

    const showDialog = useCallback((data: UserData[]) => {
        setData(data);
        setIsOpen(true);
    }, []);

    const hideDialog = useCallback(() => {
        setIsOpen(false);
    }, []);

    const dialog = <UserRolesDialog 
                        open={isOpen} 
                        data={data} 
                        onAdminToggled={onAdminToggled} 
                        onClose={hideDialog}
                    />

    return [dialog, showDialog];
}

export default useUserRolesDialog;