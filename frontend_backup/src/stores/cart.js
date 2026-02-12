import { defineStore } from 'pinia'
import request from '../utils/request'

export const useCartStore = defineStore('cart', {
  state: () => ({ count: 0 }),
  actions: {
    async refresh() {
      const res = await request.get('/cart/list')
      if (res.code === 1) {
        this.count = (res.data || []).reduce((sum, i) => sum + (i.quantity || 0), 0)
      }
    },
    setCount(n) { this.count = n }
  }
})

