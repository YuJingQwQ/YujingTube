import axios from 'axios'
import globalConfig from '../config/globalConfig'


const product = axios.create({
    baseURL: globalConfig.product,
    timeout: 10000,
    withCredentials: true
});
const user = axios.create({
    baseURL: globalConfig.user,
    timeout: 10000,
    withCredentials: true
});

const author = axios.create({
    baseURL: globalConfig.author,
    timeout: 10000,
    withCredentials: true
});

const admin = axios.create({
    baseURL: globalConfig.admin,
    timeout: 10000,
    withCredentials: true
});

export default {
    product, user, author,admin
}

