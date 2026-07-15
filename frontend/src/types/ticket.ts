// 这个票是一个统一的票，包括航班，火车，汽车等，所以字段名要通用一些
export interface Ticket {
    number: string
    //记录交通方式的id
    transportId: number
    name: string
    origin: string
    destination: string
    departureTime: string
    arrivalTime: string
    price: number
    seatRemaining: number
    // 记录交通方式的类型
    transportType: string
}
