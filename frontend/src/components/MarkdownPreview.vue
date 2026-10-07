<template><div class="markdown-body" v-html="safeHtml" /></template>

<script setup>
import { computed } from 'vue'
import DOMPurify from 'dompurify'
import { marked } from 'marked'

const props = defineProps({ text: { type: String, default: '' } })
marked.setOptions({ breaks: true, gfm: true })
const safeHtml = computed(() => DOMPurify.sanitize(marked.parse(props.text)))
</script>

