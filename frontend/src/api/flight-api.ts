import { get, post } from '@/utils/request'

export const flightApi = {
    
  // 查询航班列表，支持分页+条件查询。
  /** 这里有个问题前台和后台看到的航班信息应该不一样（用户不能看到下架的航班信息），
      后续要修改就是要做接口分离 
  */ 
  // 获取航班
  getlist: (pageNum: number, pageSize: number, params?: any) => {
    return get('/flight/list?pageNum=' + pageNum + '&pageSize=' + pageSize, params)
  },

  // 新增航班
  add: (params: any) => {
    return post('/flight/add', params)
  },

  // 修改航班
  update: (params: any) => {
    return post('/flight/update', params)
  },

  // 删除航班
  delete: (ids: any) => {
    return post('/flight/del?ids=' + ids, {})
  },
}