import { get, post } from '@/utils/request'

export const commonApi = {

    // 获取验证码
    getCaptcha: () => {
        return get('/common/getCaptcha', {})
    },


    // 上传文件
    uploadFile: (params: any) => {
        return post('/common/uploadFile', params)
    }

}
