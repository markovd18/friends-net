import { Button } from "@mui/material";
import { useRouter } from "next/router";

import SimpleNavbar from "./SimpleNavbar";


type Props = {
    onLogout: () => void,
    admin?: boolean
}

const AuthNavbar: React.FC<Props> = ({onLogout, admin}) => {   

    const router = useRouter();

    return (
        <SimpleNavbar>
            <Button
                key='logout'
                sx={{ my: 2, color: 'white', display: 'block' }}
                onClick={onLogout}
                >
                    Logout
            </Button>
            <Button
                key='friendships'
                sx={{ my: 2, color: 'white', display: 'block'}}
                onClick={() => router.push('/friendships')}
            >
                Friendships
            </Button>
            {admin && 
            <Button
                key='admin-page'
                sx={{ my: 2, color: 'white', display: 'block' }}
                onClick={() => router.push('/admin')}
            >
                Admin page
            </Button>}
        
        </SimpleNavbar>
    )
}

export default AuthNavbar;