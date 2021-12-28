import * as React from 'react'
import MuiAlert, { AlertColor, AlertProps } from '@mui/material/Alert';
import { Snackbar, SnackbarOrigin } from '@mui/material';

type Props = {
    open: boolean,
    handleClose?: () => void | undefined,
    message: string,
    severity?: AlertColor | undefined,
    position?: SnackbarOrigin | undefined
}

const Alert = React.forwardRef<HTMLDivElement, AlertProps>((
    props,
    ref,
  ) => {
    return <MuiAlert elevation={6} ref={ref} variant="filled" {...props} />;
  });
  

const SimpleSnackbar: React.FC<Props> = ({open, handleClose, message, severity, position}) => {

    return (
        <Snackbar open={open} onClose={handleClose} anchorOrigin={position}>
            <Alert onClose={handleClose} severity={severity} sx={{ width: '100%' }}>
                {message}
            </Alert>
        </Snackbar> 
    )
}

export default SimpleSnackbar;