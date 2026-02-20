<template>
  <div class="stepper" :class="{ disabled }">
    <button
      type="button"
      class="stepper-btn"
      :disabled="disabled || modelValue <= min"
      @click="decrease"
      aria-label="减少数量"
    >
      -
    </button>
    <span class="stepper-value">{{ modelValue }}</span>
    <button
      type="button"
      class="stepper-btn"
      :disabled="disabled || modelValue >= max"
      @click="increase"
      aria-label="增加数量"
    >
      +
    </button>
  </div>
</template>

<script setup>
const props = defineProps({
  modelValue: { type: Number, required: true },
  min: { type: Number, default: 1 },
  max: { type: Number, default: 99 },
  disabled: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue'])

const clamp = (value) => Math.max(props.min, Math.min(props.max, value))

const decrease = () => {
  emit('update:modelValue', clamp(props.modelValue - 1))
}

const increase = () => {
  emit('update:modelValue', clamp(props.modelValue + 1))
}
</script>

<style scoped>
.stepper {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: var(--border-strong);
  border-radius: var(--radius-pill);
  background: #fff;
  padding: 4px;
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.85);
}

.stepper.disabled {
  opacity: 0.72;
}

.stepper-btn {
  width: 26px;
  height: 26px;
  border: var(--border-strong);
  border-radius: 50%;
  background: var(--color-primary);
  color: #111;
  font-weight: 900;
  line-height: 1;
  box-shadow: 0 2px 0 rgba(0, 0, 0, 0.82);
}

.stepper-btn:disabled {
  filter: grayscale(1);
  opacity: 0.45;
}

.stepper-btn:not(:disabled):active {
  transform: translateY(1px) scale(0.98);
  box-shadow: 0 1px 0 rgba(0, 0, 0, 0.82);
}

.stepper-value {
  min-width: 22px;
  text-align: center;
  font-size: 13px;
  font-weight: 800;
  color: var(--color-text);
}
</style>
