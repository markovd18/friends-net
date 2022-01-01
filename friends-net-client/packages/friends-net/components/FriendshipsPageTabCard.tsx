import { Card, FormControl, FormHelperText, Stack, TextField } from "@mui/material";
import { useCallback, useState } from "react";
import { FriendshipsPageTab as PageTab } from "../utils/enums/RelationshipStatus";
import FriendshipsPageTab from "./FriendshipsPageTab";

type Props = {
    activeTab?: PageTab, 
    onClick?: (pageTab: PageTab) => void,
    onSearchSubmit?: (searchString: string) => void,
}

const FriendshipsPageTabCard: React.FC<Props> = ({activeTab, onClick, onSearchSubmit}) => {
    
    const [searchValue, setSearchValue] = useState("");
    const [searchTooShort, setSearchTooShort] = useState(false);
    

    const handleSearchChange = useCallback((event: React.ChangeEvent<HTMLTextAreaElement>) => {
        event.preventDefault();
        setSearchValue(event.target.value);
    }, [searchValue]);

    
    const handleSubmit = useCallback(async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (searchValue.length < 3) {
            setSearchTooShort(true);
            return;
        }

        if (searchTooShort) setSearchTooShort(false);
        if (onSearchSubmit) onSearchSubmit(searchValue);
        setSearchValue("");
    }, [searchValue]);
    
    return (
        <Card>
            <Stack spacing={0} padding={1}>
                <FriendshipsPageTab 
                    type={PageTab.FRIEND_REQUESTS} 
                    active={activeTab === PageTab.FRIEND_REQUESTS}
                    onClick={onClick}    
                />
                <FriendshipsPageTab 
                    type={PageTab.FRIENDS} 
                    active={activeTab === PageTab.FRIENDS}
                    onClick={onClick}    
                />
                <FriendshipsPageTab 
                    type={PageTab.BLOCKED} 
                    active={activeTab === PageTab.BLOCKED}
                    onClick={onClick}
                />
            </Stack>

            <form onSubmit={handleSubmit}>
                <FormControl error={searchTooShort} fullWidth>
                    <TextField 
                        variant="filled" 
                        size="small" 
                        fullWidth 
                        placeholder="Find new friends"
                        value={searchValue}
                        onChange={handleSearchChange}
                        aria-describedby="error-desc"
                        error={searchTooShort}
                    />
                    {searchTooShort && <FormHelperText id="error-desc">At least 3 characters required</FormHelperText>}

                </FormControl>
            </form>
            
        </Card>
    )
}

export default FriendshipsPageTabCard;