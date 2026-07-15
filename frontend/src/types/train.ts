export interface TrainUserVO {
    // 记录自身id为了订单记录服务
    id: number
    trainNumber: string
    trainName: string
    origin: string
    destination: string
    departureTime: string
    arrivalTime: string
    price: number
    seatRemaining: number
}

