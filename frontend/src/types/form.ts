// 定义表单中Field的各个字段类型
export interface Field {
  label: string
  prop: string
  type: 'input' | 'password' | 'select' | 'upload' | 'datetime' | 'textarea' 
  readonly?: boolean
  options?: { label: string; value: any }[]
}


