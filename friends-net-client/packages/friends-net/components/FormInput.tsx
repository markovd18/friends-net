import { FormControl, FormHelperText, Input, InputLabel } from '@mui/material'

type Props = {
    variant?: 'standard' | 'filled' | 'outlined' | undefined,
    required?: boolean | undefined,
    id: string,
    type?: string | undefined,
    label?: string | undefined,
    onChange?: (event: React.ChangeEvent<HTMLTextAreaElement>) => void,
    value?: string | undefined,
    helperText?: string | undefined,
    error?: boolean | undefined,
    errorText?: string | undefined,
}

const FormInput: React.FC<Props> = ({
    id,
    onChange,
    required,
    type,
    label,
    value,
    variant,
    helperText,
    error,
    errorText
}) => {

    return (
        <FormControl variant={variant} required={required} fullWidth error={error}>
            <InputLabel htmlFor={id}>{label}</InputLabel>
            <Input 
                id={id} 
                type={type} 
                aria-describedby={helperText && `${id}-desc`} 
                value={value} 
                onChange={onChange}
            />
            {helperText && <FormHelperText id={`${id}-desc`}>{helperText}</FormHelperText>}
            {error && <FormHelperText error>{errorText}</FormHelperText>}
        </FormControl>
    )
}

export default FormInput;