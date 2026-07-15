

export interface OrderUserVO {
    id: number
    transportType: string
    number: string
    origin: string
    destination: string
    departureTime: string
    arrivalTime: string
    seatCount: number
    totalPrice: number
    status: 'UNPAID' | 'PAID' | 'CANCELED'
}

// 订单查询参数
export interface OrderQuery {
  number?: string
  origin?: string
  destination?: string
  transportType?: 'BUS' | 'TRAIN' | 'FLIGHT'| undefined
  userId?: number
  pageNum?: number
  pageSize?: number
  departureTime?: string
  arrivalTime?: string
  status?: 'UNPAID' | 'PAID' | 'CANCELED'
}

export interface OptionalTicketVO {
    id: number
    number: string
    origin: string
    destination: string
    departureTime: string 
    arrivalTime: string
    price: number
    seatRemaining: number
    diffPrice: number
}

export interface ChangeNewTicket{
    oldId: number
    transportId: number
    transportType: 'BUS' | 'TRAIN' | 'FLIGHT' | undefined
    seatCount: number
}
