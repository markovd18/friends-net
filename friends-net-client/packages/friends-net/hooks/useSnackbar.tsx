import { AlertColor } from "@mui/material";
import { useCallback, useState } from "react";
import SimpleSnackbar from "../components/SimpleSnackbar";

const useSnackbar = (): [JSX.Element, (message: string, severity: AlertColor) => void] => {

    const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false);
    const [snackbarMessage, setSnackbarMessage] = useState<string>("");
    const [snackbarSeverity, setSnackbarSeverity] = useState<AlertColor>();

    const handleSnackbarClose = useCallback(() => {
        setSnackbarOpen(false);
    }, []);

    const showSnackbar = useCallback((message: string, severity: AlertColor) => {
        setSnackbarMessage(message);
        setSnackbarSeverity(severity);
        setSnackbarOpen(true);
    }, []);

    const Snackbar = <SimpleSnackbar 
                        open={snackbarOpen} 
                        message={snackbarMessage} 
                        severity={snackbarSeverity} 
                        handleClose={handleSnackbarClose}
                    />

    return [ Snackbar, showSnackbar ];
} 

export default useSnackbar;