export interface FlightUserVO {
    // 记录自身id为了订单记录服务
    id: number
    flightNumber: string
    flightName: string
    logourl: string
    origin: string
    destination: string
    departureTime: string
    arrivalTime: string
    price: number
    seatRemaining: number
}