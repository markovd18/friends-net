import { UserIdentificationDataVO } from "@markovda/fn-api";
import { List } from "@mui/material";
import { useMemo } from "react";
import OnlineUserCard from "./OnlineUserCard";

type Props = {
    data: UserIdentificationDataVO[],
    onItemClick?: (userData: UserIdentificationDataVO) => void
}

const OnlineUsersList: React.FC<Props> = ({data, onItemClick}) => {

    const listData = useMemo(() => {
        return data.map(user => (
            <OnlineUserCard 
                userData={user} 
                onClick={onItemClick} 
                key={user.login}
            />
        ));
    }, [data]);

    return (
        <List
            sx={{maxWidth: 300, position: "fixed", minWidth: 300, marginTop: 30, maxHeight: 500, overflow: 'auto'}} 
        >
            {listData}
        </List>
    )
}

export default OnlineUsersList;