import { Tooltip } from "@mui/material"
import NewReleasesIcon from '@mui/icons-material/NewReleases';

type Props = {
    color?: string
}

const AnnouncementIcon: React.FC<Props> = ({color}) => {

    return (
        <Tooltip title='Announcement'>
            <NewReleasesIcon htmlColor={color}/>
        </Tooltip>
    )
}

export default AnnouncementIcon;