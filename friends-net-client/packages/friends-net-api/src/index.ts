/* tslint:disable */
/* eslint-disable */
/**
 * Friends Net API
 * Frineds Net server API. 
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

import * as Api from './api'
import axios from "axios"

export * from "./configuration";
export * from "./model";

let baseURL = 'http://localhost:8080'   //TODO env variable with end server address
let baseAxios = axios.create({baseURL, headers: { Pragma: "no-cache"}})

const AuthApi = new Api.AuthenticationControllerApi(undefined, baseURL, baseAxios)
const FriendshipApi = new Api.UserRelationshipControllerApi(undefined, baseURL, baseAxios);
const UserSearchApi = new Api.UserSearchControllerApi(undefined, baseURL, baseAxios);
const PostApi = new Api.PostControllerApi(undefined, baseURL, baseAxios);
const AdminApi = new Api.AdminControllerApi(undefined, baseURL, baseAxios);

export {
    AuthApi,
    FriendshipApi,
    UserSearchApi,
    PostApi,
    AdminApi
}