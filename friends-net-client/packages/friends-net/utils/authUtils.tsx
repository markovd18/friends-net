import { EnumUserRole } from "@markovda/fn-api"


export type UserData = {
    name: string, 
    login: string,
    accessToken: string,
    roles: EnumUserRole[]
}

export const isUserAdmin = (userData: UserData) => {
    return hasAdminRole(userData.roles);
}

export const hasAdminRole = (roles: EnumUserRole[]) => {
    return roles.some(role => role === EnumUserRole.ADMIN);
}