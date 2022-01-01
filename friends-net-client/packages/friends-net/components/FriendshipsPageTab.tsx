import { Card, CardActionArea, CardContent, Typography } from "@mui/material";
import { useCallback } from "react";
import { FriendshipsPageTab as Type, getCaption } from "../utils/enums/RelationshipStatus";

type Props = {
    active?: boolean,
    type: Type,
    onClick?: (pageTab: Type) => void
}

const FriendshipsPageTab: React.FC<Props> = ({active, type, onClick}) => {

    const style = active ? {backgroundColor: 'ActiveCaption'} : {};

    const handleClick = useCallback(() => {
        if (onClick) {
            onClick(type)
        }
    }, [type]);
    
    return (
        <Card elevation={0} sx={style}>
            <CardActionArea onClick={handleClick} >
                <CardContent>
                    <Typography variant="h5" color='text.secondary'>
                        {getCaption(type)}
                    </Typography>
                </CardContent>
            </CardActionArea>
        </Card>
    )
}

export default FriendshipsPageTab;