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

export const hasUserRole = (userData: UserData, role: EnumUserRole) => {
    return containsRole(userData.roles, role);
}

export const containsRole = (roles: EnumUserRole[], role: EnumUserRole) => {
    return roles.some(r => r === role);
}