<template>
  <article class="cart-row">
    <div class="check-col" @click.stop>
      <el-checkbox :model-value="item.selected" @change="onToggleSelected" />
    </div>

    <img :src="item.imageUrl || placeholder" class="thumb" :alt="item.goodsName || '商品图片'" />

    <div class="main">
      <div class="row-head">
        <h3 class="name">{{ item.goodsName }}</h3>
        <button type="button" class="delete-btn" :disabled="saving" @click="emit('remove')">删除</button>
      </div>

      <p class="tag">{{ tagText }}</p>

      <div class="price-line">
        <span class="unit-price">单价 ¥{{ formatMoney(item.price) }}</span>
        <span class="subtotal">小计 ¥{{ subtotal }}</span>
      </div>

      <div class="row-foot">
        <QuantityStepper
          :model-value="qty"
          :min="1"
          :max="maxQuantity"
          :disabled="saving"
          @update:model-value="onQuantityChange"
        />
        <span v-if="saving" class="saving">更新中...</span>
      </div>

      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import QuantityStepper from './QuantityStepper.vue'

const props = defineProps({
  item: { type: Object, required: true },
  saving: { type: Boolean, default: false },
  errorMessage: { type: String, default: '' },
  maxQuantity: { type: Number, default: 99 }
})

const emit = defineEmits(['change-quantity', 'remove', 'toggle-selected'])

const placeholder = 'https://placehold.co/160x160/fff4d9/999?text=Snack'

const qty = computed(() => Math.max(1, Number(props.item?.quantity || 1)))
const subtotal = computed(() => formatMoney(Number(props.item?.price || 0) * qty.value))

const tagText = computed(() => {
  if (props.item?.spec) return props.item.spec
  if (props.item?.categoryName) return props.item.categoryName
  return '门店热卖'
})

const onToggleSelected = (checked) => {
  emit('toggle-selected', Boolean(checked))
}

const onQuantityChange = (next) => {
  emit('change-quantity', Number(next) || 1)
}

function formatMoney(value) {
  return Number(value || 0).toFixed(2)
}
</script>

<style scoped>
.cart-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  background: #fff;
  border: var(--border-strong);
  border-radius: var(--radius-card);
  box-shadow: 0 3px 0 rgba(0, 0, 0, 0.88);
  padding: 10px;
}

.check-col {
  padding-top: 6px;
  flex-shrink: 0;
}

.thumb {
  width: 72px;
  height: 72px;
  border-radius: 14px;
  border: var(--border-strong);
  object-fit: cover;
  background: #fff;
  flex-shrink: 0;
}

.main {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.row-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
}

.name {
  margin: 0;
  font-size: 14px;
  line-height: 1.35;
  color: var(--color-text);
  font-weight: 800;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.delete-btn {
  border: var(--border-strong);
  border-radius: var(--radius-pill);
  background: #fff;
  color: rgba(0, 0, 0, 0.62);
  font-size: 11px;
  font-weight: 700;
  line-height: 1;
  padding: 5px 10px;
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.8);
  white-space: nowrap;
}

.delete-btn:disabled {
  opacity: 0.5;
}

.delete-btn:not(:disabled):active {
  transform: translateY(1px) scale(0.98);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.8);
}

.tag {
  margin: 0;
  width: fit-content;
  border: var(--border-strong);
  border-radius: var(--radius-pill);
  background: #fffbe6;
  color: rgba(0, 0, 0, 0.64);
  font-size: 10px;
  font-weight: 700;
  line-height: 1;
  padding: 4px 8px;
}

.price-line {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.unit-price {
  color: rgba(0, 0, 0, 0.66);
  font-size: 12px;
  font-weight: 600;
}

.subtotal {
  color: var(--color-accent);
  font-size: 13px;
  font-weight: 900;
}

.row-foot {
  display: flex;
  align-items: center;
  gap: 8px;
}

.saving {
  color: rgba(0, 0, 0, 0.48);
  font-size: 11px;
  font-weight: 700;
}

.error {
  margin: 0;
  color: #d62828;
  font-size: 11px;
  line-height: 1.3;
  font-weight: 700;
}
</style>
