import React, { PropsWithChildren } from 'react';
import { createContext, useCallback, useState } from 'react';
import { useSnackbar } from '../hooks';

type UserData = {
    name?: string, 
    login?: string
}

type UserContextData = {
    userData: UserData,
    loginUser: (userData: UserData) => void,
    logoutUser: () => void
}

const UserContext = createContext<UserContextData>({ userData: {}, loginUser: () => {}, logoutUser: () => {}});

const UserDataProvider = ({children}) => {

    const [userData, setUserData] = useState<UserData>({});
    const [Snackbar, showSnackbar] = useSnackbar();

    const loginUser = useCallback((userData: UserData) => {
        setUserData(userData);
    }, []);

    const logoutUser = useCallback(() => {
        setUserData({});
        showSnackbar('Successfully logged out.', 'success');
    }, []);

    return (
        <UserContext.Provider value={{userData, loginUser, logoutUser}}>
            {children}
            {Snackbar}
        </UserContext.Provider>
    )
}

export {
    UserContext,
    UserDataProvider
};