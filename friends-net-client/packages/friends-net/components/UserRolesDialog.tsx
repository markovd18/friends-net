import { 
    Button, 
    Dialog, 
    DialogActions, 
    DialogContent, 
    DialogTitle, 
    Switch, 
    Table, 
    TableBody, 
    TableCell, 
    TableHead, 
    TableRow 
} from "@mui/material";
import { useEffect, useMemo, useState } from "react";

export type UserData = {
    login: string,
    name: string,
    isAdmin: boolean
}

type Props = {
    open: boolean,
    onClose?: () => void,
    data: UserData[],
    onAdminToggled?: (login: string, isAdmin: boolean) => void,
}


const UserRolesDialog: React.FC<Props> = ({open, data, onClose, onAdminToggled}) => {

    const [formData, setFormData] = useState(data);

    useEffect(() => {
        setFormData(data);
    }, [data]);

    const handleAdminToggled = (target: UserData) => {
        setFormData(prevState => {
            const newState = [...prevState];
            newState.filter(user => user.login === target.login).forEach(user => user.isAdmin = !user.isAdmin);
            return newState;
        });

        if (onAdminToggled) {
            onAdminToggled(target.login, target.isAdmin);
        }
    }

    const content = useMemo(() => {
        return formData.map(user => (
                <TableRow key={user.login}>
                    <TableCell>{`${user.name} (${user.login})`}</TableCell>
                    <TableCell><Switch checked={user.isAdmin} onChange={() => handleAdminToggled(user)}/></TableCell>
                </TableRow>
            ));
    }, [formData]);

    return (
        <Dialog open={open} onClose={onClose}>
            <DialogTitle>User role management</DialogTitle>
            <DialogContent>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>User</TableCell>
                            <TableCell>Is admin?</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {content}
                    </TableBody>
                </Table>
            </DialogContent>
            <DialogActions>
                <Button variant='outlined' onClick={onClose}>Close</Button>
            </DialogActions>
        </Dialog>
    )
} 

export default UserRolesDialog;