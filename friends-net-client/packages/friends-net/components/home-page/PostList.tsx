import { PostVO } from "@markovda/fn-api"
import { Card, CardContent, SxProps, Theme } from "@mui/material";
import { useMemo } from "react"
import PostCard from "./PostCard";


type Props = {
    data: PostVO[],
    elevation?: number,
    style?: SxProps<Theme>,
}

const PostList: React.FC<Props> = ({data, elevation, style}) => {

    const posts = useMemo(() => {
        return data.map((post, index) => (
            <PostCard data={post} key={index} style={{marginTop: 4, marginBottom: 4}} />
        ));
    }, [data]);

    return (
        <Card elevation={elevation} sx={style}>
            <CardContent>
                {posts}
            </CardContent>
        </Card>
    )
}

export default PostList;