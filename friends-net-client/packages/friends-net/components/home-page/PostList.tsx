import { PostVO } from "@markovda/fn-api"
import { Card, CardContent, Stack, SxProps, Theme } from "@mui/material";
import { useMemo } from "react"
import PostCard from "./PostCard";


type Props = {
    data: PostVO[],
    style?: SxProps<Theme>,
}

const PostList: React.FC<Props> = ({data, style}) => {

    const posts = useMemo(() => {
        return data.map((post, index) => (
            <PostCard data={post} key={index} style={{marginTop: 4, marginBottom: 4}} />
        ));
    }, [data]);

    return (
        <Stack sx={style}>
            {posts}
        </Stack>
    )
}

export default PostList;