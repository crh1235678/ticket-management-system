export type FilterItemType = 'input' | 'date' | 'select'

export interface FilterOption {
  label: string
  value: string | number
}

export interface FilterConfig {
  label: string
  prop: string
  type: FilterItemType
  placeholder?: string
  options?: FilterOption[]
}