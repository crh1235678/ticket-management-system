//封装axios请求

import axios, { type AxiosRequestConfig } from 'axios';
import { saveLocalStorage, getLocalStorage, clearLocalStorage } from '@/utils/utils';
import Constants from '@/utils/constants';
import { ElMessage, ElMessageBox } from 'element-plus';

// token的名称 需要和服务端 配置的对应
const TOKEN_NAME = 'vaeeetoken';

// 创建axios对象，统一baseURL
const request = axios.create({
    baseURL: import.meta.env.VITE_API_URL,
});

// 退出系统，清除本地token，并跳转页面
function logout() {
    clearLocalStorage();
    location.href = '/';
}


// ------------------------------ 请求拦截器 ------------------------------
// 添加注册请求拦截器
//request.interceptors.request.use() 这个方法的设计，要求传入两个函数作为参数：
//第一个函数：请求成功前的处理函数
//第二个函数：请求配置出错时的处理函数
request.interceptors.request.use(
    //成功处理
    (config) => {
        let token = getLocalStorage(Constants.TOKEN);
        if (token) {
            //把token加入请求头里
            config.headers[TOKEN_NAME] = token;
        } else {
            delete config.headers[TOKEN_NAME];
        }
        return config;
    },
    // 失败处理
    // config内部出错了，比如配置错误会进入这里
    // Promise.reject(error)里有throw new Error()
    (error: any) => {
        return Promise.reject(error);
    }
);

// ------------------------------ 响应拦截器 ------------------------------
// 添加注册响应拦截器
// 响应拦截器一定是在“请求已经发出，并且服务器有回应之后”才会触发
// response 是后端传给前端的响应数据包括状态码（status）、响应头（headers）、响应体（data）等
// response 是：axios 封装的 HTTP 响应对象
// response = {
//  data,     // 响应体（后端真正返回的内容）
//  status,   // HTTP 状态码
//  headers,  // 响应头
//  config,
//  request
//}

//promise的resolve是成功回调执行的是.then()，reject是失败回调执行的是.catch()
request.interceptors.response.use(
    (response) => {
        // 根据content-type，判断是否为json数据
        let contentType = response.headers['content-type'] ? response.headers['content-type'] : response.headers['Content-Type'];
        //console.log('contentType:', contentType);
        // ===严格相等判断，不会进行类型转换
        if (contentType.indexOf('application/json') === -1) {
            // 这个就是跳过解包。此时返回的还是完整的 AxiosResponse 对象，真实数据在 response.data 里
            return Promise.resolve(response);
        }

        // 如果返回的数据类型是 Blob
        if (response.data && response.data instanceof Blob) {
            return Promise.reject(response.data);
        }

        // 正常 JSON 响应，提取响应体数据
        // 这里把 AxiosResponse.data 解包赋给 res，组件拿到的就是后端返回的数据，不再需要 .data
        const res = response.data;
        //后端@SaCheckLogin 负责校验当前请求是否携带有效 token。抛出11012或11011.
        if (res.code && res.code !== 200) {
            // `token` 过期或者账号已在别处登录
            if (res.code === 11012 || res.code === 11011) {
                // 清空信息
                ElMessage.closeAll();
                // 提示错误信息
                ElMessage.error('您没有登录，请重新登录');
                setTimeout(logout, 300);
                return Promise.reject(response);
            }
            // 长时间未操作系统，需要重新登录
            if (res.code === 30001) {
                ElMessageBox.confirm('您需要重新登陆', '确认退出登录', {
                    confirmButtonText: '重新登陆',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    logout()
                })
                setTimeout(logout, 3000);
                return Promise.reject(response);
            }
            // -----------------  角色权限不足 -----------------
            if (res.code === 11013 || res.code === 403 || res.message?.includes('无此角色')) {
                ElMessage.closeAll();

                // 显示提示信息
                ElMessage.warning('您没有权限访问该页面，请使用正确账号登录');

                // 延迟 200~300 毫秒再执行 logout + 跳转
                setTimeout(() => {
                    logout();
                }, 3000);

                return Promise.reject(res);
            }
            ElMessage.closeAll();
            if (res.message) {
                ElMessage.error(res.message);
            }
            return Promise.reject(response);
        } else {
            // 正常返回数据
            return Promise.resolve(res);
        }
    },

    // 客户端没有得到合法成功的响应
    (error) => {
        // 对响应错误做点什么
        if (error.message.indexOf('timeout') !== -1) {
            ElMessage.closeAll();
            ElMessage.error('网络超时');
        } else if (error.message === 'Network Error') {
            ElMessage.closeAll();
            ElMessage.error('网络连接错误');
        } else if (error.message.indexOf('Request') !== -1) {
            ElMessage.closeAll();
            ElMessage.error('网络发生错误');
        }
        return Promise.reject(error);
    }
);


// ================================ 通用请求, get, post ================================

/**
 * .request() 是 axios 的核心方法,是用来发送HTTP请求的
 * 自动触发已注册的请求和响应拦截器
 *  返回一个 Promise，resolve 成功响应，reject 错误信息
 * 
 *  当.request() 方法被调用时,
 * 会触发已经注册的请求拦截器,发送请求,触发响应拦截器,返回响应数据
 *
 */

/**
 * 通用请求封装
 * @param config
 */
export const http = (config: AxiosRequestConfig<any>) => {
    return request.request(config);
};

/**
 * get请求
 */
export const get = (url: string, params: any) => {
    return http({ url, method: 'get', params });
};

/**
 * post请求
 */
export const post = (url: string, data: {}) => {
    return http({
        data,
        url,
        method: 'post',
    });
};
