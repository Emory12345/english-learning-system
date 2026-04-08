import { ElMessage } from 'element-plus'

export const useWatchHistory = () => {
  const saveWatchHistory = (video: any, courseType: string) => {
    try {
      const historyKey = 'watchHistory'
      let history = []
      // 从localStorage获取现有历史
      const existingHistory = localStorage.getItem(historyKey)
      if (existingHistory) {
        history = JSON.parse(existingHistory)
      }
      // 创建新的观看记录
      const newRecord = {
        courseId: video.id,
        courseTitle: video.title,
        courseImage: video.thumbnail || video.image || '',
        courseType: courseType,
        videoUrl: video.videoUrl || '',
        lastWatchTime: new Date().toISOString(),
        currentChapter: '1'
      }
      // 移除已存在的相同视频记录
      history = history.filter((item: any) => item.courseId !== video.id)
      // 添加新记录到开头
      history.unshift(newRecord)
      // 只保留最近4条记录
      if (history.length > 4) {
        history = history.slice(0, 4)
      }
      // 保存到localStorage
      localStorage.setItem(historyKey, JSON.stringify(history))
      console.log('Watch history saved for video:', video.title)
      console.log('Current watch history:', history)
    } catch (error) {
      console.error('Failed to save watch history:', error)
    }
  }

  return {
    saveWatchHistory
  }
}
