import { PostVO } from "@markovda/fn-api";
import { Card, CardContent, CardHeader, SxProps, Theme, Typography } from "@mui/material";
import { style } from "@mui/system";
import AnnouncementIcon from "../AnnouncementIcon";

type Props = {
    data: PostVO,
    style?: SxProps<Theme>
}

const PostCard: React.FC<Props> = ({data, style}) => {

    return (
        <Card sx={style}>
            <CardHeader 
                title={data.author?.name}
                subheader={data.author?.login}
                titleTypographyProps={{component: 'div', variant: 'h6'}}
                avatar={
                    data.isAnnouncement ? <AnnouncementIcon color='blue' /> : undefined
                }
            />
            <CardContent>
                <Typography variant='body1'>
                    {data.content}
                </Typography>
            </CardContent>
        </Card>
    )
}

export default PostCard;