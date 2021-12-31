import { Container } from "@mui/material";

const PageContentContainer: React.FC<{}> = ({children}) => {

    return (
        <Container 
            maxWidth='lg' 
            sx={{flex: 1, display: "flex", flexDirection: "column", marginTop: 20, marginBottom: 10}}
        >
            {children}
        </Container>
    )
} 

export default PageContentContainer;