import { AddCircle } from "@mui/icons-material";
import { Stack, Button } from "@mui/material";
import AdminPanelSettingsIcon from "@mui/icons-material/AdminPanelSettings"
type Props = {
    showAdminButtons?: boolean,
    onNewPostClick?: () => void,
    onAdminRolesClick?: () => void,
}

const BaseCardButtons: React.FC<Props> = ({
    showAdminButtons,
    onNewPostClick,
    onAdminRolesClick
}) => {

    return (
        <Stack spacing={2}>
            <Button variant='contained' size='small' color='primary' onClick={onNewPostClick}>
                <AddCircle />
                New post
            </Button>
            {showAdminButtons && 
            <Button variant='contained' size='small' color='secondary' onClick={onAdminRolesClick}>
                <AdminPanelSettingsIcon />
                Set admin roles
            </Button>}
        </Stack>
    )
}

export default BaseCardButtons;