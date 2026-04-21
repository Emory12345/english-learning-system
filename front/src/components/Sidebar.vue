<template>
  <div 
    class="sidebar fixed left-0 top-0 h-full bg-white/80 backdrop-blur-lg border-r border-gray-200 z-30"
    :class="{ 'w-64': !collapsed, 'w-16': collapsed }"
  >
    <!-- 品牌Logo和折叠按钮 -->
    <div class="flex items-center justify-between p-4 border-b border-gray-200">
      <div class="flex items-center space-x-3">
        <div class="w-8 h-8 rounded-lg bg-[#165DFF] flex items-center justify-center text-white font-bold text-xl">
          E
        </div>
        <span v-if="!collapsed" class="text-lg font-semibold text-gray-800">在线英语学习系统</span>
      </div>
      <button 
        @click="toggleCollapse" 
        class="p-2 rounded-lg hover:bg-gray-100 transition-colors duration-200"
      >
        <el-icon>
          <component :is="collapsed ? 'Menu' : 'Close'" />
        </el-icon>
      </button>
    </div>

    <!-- 中部菜单 -->
    <nav class="py-4">
      <ul class="space-y-1 px-3">
        <li v-for="item in menuItems" :key="item.id">
          <a
            :href="item.path"
            class="flex items-center py-2.5 px-3 rounded