import { Dialog, DialogContent, DialogTitle } from "@mui/material";

type Props = {
    open: boolean,
    onClose?: () => void,
}


const UserRolesDialog: React.FC<Props> = ({open}) => {

    return (
        <Dialog open={open}>
            <DialogTitle>User role management</DialogTitle>
            <DialogContent>

            </DialogContent>
        </Dialog>
    )
} 

export default UserRolesDialog;