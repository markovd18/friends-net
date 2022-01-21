import { Stack, FormGroup, FormControlLabel, ButtonGroup, Button, Switch } from "@mui/material";

type Props = {
    isAdmin?: boolean,
    onClose?: () => void,
    onSubmit?: (event: React.FormEvent<HTMLFormElement>) => void,
    isAnnouncement?: boolean,
    onAnnouncementToggle?: () => void,
}

const NewPostModalActions: React.FC<Props> = ({
    isAdmin, 
    onClose, 
    onSubmit, 
    isAnnouncement,
    onAnnouncementToggle,
}) => {

    const renderAnnouncementSwitch = (): JSX.Element => {
        return (
            <Switch 
                title="Announcement" 
                checked={isAnnouncement} 
                onChange={onAnnouncementToggle} 
            />
        );
    }

    return (
        <form onSubmit={onSubmit}>
            <Stack direction='row'>
                <FormGroup>
                    {isAdmin && 
                    <FormControlLabel control={renderAnnouncementSwitch()} label="Announcement"/>}
                </FormGroup>
                <ButtonGroup >
                    <Button variant='outlined' onClick={onClose}>Cancel</Button>
                    <Button variant='contained' type='submit'>Create</Button>
                </ButtonGroup>
            </Stack>
        </form>
    )
}

export default NewPostModalActions;