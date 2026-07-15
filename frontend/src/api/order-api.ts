import { get, post } from '@/utils/request'

export const orderApi = {
  // 生成订单
  create: (params: any) => {
    return post('/order/create', params)
  },
  // 取消订单
  cancel: (orderId: number) => {
    return post('/order/cancel?orderId=' + orderId, {})
  },
  // 退票
  refund: (orderId: number) => {
    return post('/order/refund?orderId=' + orderId, {})
  },
  // 支付订单
  pay: (orderId: number) => {
    return post('/order/pay?orderId=' + orderId, {})
  },
  // 修改订单
  update: (params: any) => {
    return post('/order/modify', params)
  },
  // 删除订单
  delete: (ids: any) => {
    return post('/order/del?ids=' + ids, {})
  },
  // 罗列可供选择的改签票
  getOptionalTickets: (pageNum: number, pageSize: number, params?: any) => {
    return get('/order/change/list?pageNum=' + pageNum + '&pageSize=' + pageSize, params)
  },
  // 改签订单
  changeConfirm: (params: any) => {
    return post('/order/change/confirm', params)
  },

  // ✅ 通用查询接口（核心）
  getList: (pageNum: number, pageSize: number, params?: any) => {
    return get('/order/list?pageNum=' + pageNum + '&pageSize=' + pageSize, params)
  },

  // ✅ 用户订单
  getMyOrder: (pageNum: number, pageSize: number) => {
    return orderApi.getList(pageNum, pageSize)
  },

  // ✅ 管理员订单
  getAllOrder: (pageNum: number, pageSize: number, params?: any) => {
    return orderApi.getList(pageNum, pageSize, params)
  }

}
