import axios from 'axios'
import globalConfig from '../config/globalConfig'


const product = axios.create({
    baseURL: globalConfig.product,
    timeout: 60000,
    withCredentials: true,
    headers: { "token": window.localStorage.loginJwt ? window.localStorage.loginJwt : '' }
});
const user = axios.create({
    baseURL: globalConfig.user,
    timeout: 60000,
    withCredentials: true,
    headers: { "token": window.localStorage.loginJwt ? window.localStorage.loginJwt : '' }
});

const author = axios.create({
    baseURL: globalConfig.author,
    timeout: 60000,
    withCredentials: true,
    headers: { "token": window.localStorage.loginJwt ? window.localStorage.loginJwt : '' }
});

export default {
    product, user, author
}

