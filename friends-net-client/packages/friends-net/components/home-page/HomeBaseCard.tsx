import { Card, CardContent } from "@mui/material";
import BaseCardButtons from "./BaseCardButtons";
import BaseCardUserIdentification from "./BaseCardUserIdentification";

type Props = {
    name: string,
    login: string,
    isAdmin?: boolean,
    onNewPostClick: () => void,
    onAdminRolesClick?: () => void,
}

const HomeBaseCard: React.FC<Props> = ({name, login, isAdmin, onNewPostClick, onAdminRolesClick}) => {

    return (
        <Card sx={{ maxWidth: 300, 
            minWidth: 100, padding: 4, flex: 1, 
           display: "flex", flexDirection: "column", 
           justifyContent: "center", alignContent: "center",
           position: "fixed"}}>
           <CardContent>
                <BaseCardUserIdentification 
                    name={name}
                    login={login}
                />
                <BaseCardButtons 
                    showAdminButtons={isAdmin}
                    onAdminRolesClick={onAdminRolesClick}
                    onNewPostClick={onNewPostClick}
                />
           </CardContent>
       </Card>
    )
}

export default HomeBaseCard;