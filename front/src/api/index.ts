// API 服务层

// 后端API基础URL
const API_BASE_URL = 'http://localhost:8080';

// 通用请求方法
async function request<T>(url: string, options: RequestInit & { params?: Record<string, any> } = {}): Promise<T> {
  // 构建请求头
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...options.headers,
  };
  let requestUrl = url;
  if (options.params) {
    const searchParams = new URLSearchParams();
    Object.entries(options.params).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        searchParams.append(key, String(value));
      }
    });
    const paramsString = searchParams.toString();
    if (paramsString) {
      requestUrl += `?${paramsString}`;
    }
  }
  
  // 只有非认证请求才添加token
  const isAuthRequest = url.startsWith('/auth/');
  if (!isAuthRequest) {
    const token = localStorage.getItem('token');
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
  }
  
  // 移除params字段，因为fetch不支持
  const { params, ...fetchOptions } = options;
  
  console.log('Request URL:', `${API_BASE_URL}${requestUrl}`);
  console.log('Request Headers:', headers);
  console.log('Request Options:', fetchOptions);
  
  const response = await fetch(`${API_BASE_URL}${requestUrl}`, {
    ...fetchOptions,
    headers,
  });

  console.log('Response Status:', response.status);
  console.log('Response Headers:', response.headers);
  
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  const data = await response.json();
  console.log('Response Data:', data);
  // 处理后端返回的ResponseDTO格式
  if (data.code === 200) {
    return data.data as T;
  } else if (Array.isArray(data) || typeof data === 'object') {
    // 直接返回数据，适用于后端直接返回列表或对象的情况
    return data as T;
  } else {
    throw new Error(data.message || '请求失败');
  }
}

// API方法
export const api = {
  // 通用请求方法
  get: <T>(url: string, params?: Record<string, any>) => request<T>(url, { method: 'GET', params }),
  post: <T>(url: string, data?: any) => request<T>(url, { method: 'POST', body: JSON.stringify(data) }),
  // 认证相关
  auth: {
    login: (data: { email: string; password: string; role: string }) =>
      request<{ token: string; role: string; userInfo: { id: number; name: string; email: string } }>('/auth/login', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    register: (data: { name: string; email: string; password: string; role: string }) =>
      request<{ message: string }>('/auth/register', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
  },

  // 课程相关
  courses: {
    getList: () => request<any[]>('/course/list'),
    getDetail: (id: string) => request<any>(`/course/${id}`),
    create: (data: any) => request<any>('/course', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
    update: (id: string, data: any) => request<any>(`/course/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
    delete: (id: string) => request<{ message: string }>(`/course/${id}`, {
      method: 'DELETE',
    }),
    enroll: (id: string) => request<any>(`/course/enroll/${id}`, {
      method: 'POST',
    }),
    unenroll: (id: string) => request<any>(`/course/unenroll/${id}`, {
      method: 'POST',
    }),
    getEnrolled: () => request<any[]>('/course/enrolled'),
    getTeacherCourses: () => request<any[]>('/course/teacher'),
  },

  // 作业相关
  homeworks: {
    publish: (data: { title: string; content: string; category: string; type: string; image?: string }) => request<any>('/homework/publish', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
    getAll: () => request<any[]>('/homework/'),
    getByCategory: (category: string) => request<any[]>(`/homework/category/${category}`),
    getByCategoryAndType: (category: string, type: string) => request<any[]>(`/homework/category/${category}/type/${type}`),
    getAllSubmissions: () => request<any[]>('/homework/submissions/all'),
    getMySubmissions: () => request<any[]>('/homework/submissions/my'),
    submit: (data: { homeworkId: string; content: string; image?: string; audio?: string }) => request<any>('/homework/submit', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
    correct: (data: { submissionId: string; score: number; feedback: string }) => request<any>('/homework/grade', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
    getSubmissions: (homeworkId: string) => request<any[]>(`/homework/submissions/${homeworkId}`),
    getTeacherHomework: () => request<any[]>('/homework/teacher'),
    getTeacherSubmissions: () => request<any[]>('/homework/submissions/teacher'),
    editHomework: (id: string, data: any) => request<any>(`/homework/edit/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
    deleteHomework: (id: string) => request<{ message: string }>(`/homework/delete/${id}`, {
      method: 'DELETE',
    }),
  },

  // 用户相关
  users: {
    getList: () => request<any[]>('/admin/users'),
    delete: (id: string) => request<{ message: string }>(`/admin/users/${id}`, {
      method: 'DELETE',
    }),
    update: (id: string, data: any) => request<any>(`/admin/users/${id}`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),
    getStats: () => request<any>('/admin/users/stats'),
    getGrowth: () => request<any>('/admin/users/growth'),
  },
  
  // 管理员相关
  admin: {
    getPendingCourses: () => request<any[]>('/admin/courses/pending'),
    auditCourse: (data: { courseId: string; status: string }) => request<any>('/admin/courses/audit', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
    deleteCourse: (id: string) => request<{ message: string }>(`/admin/courses/${id}`, {
      method: 'DELETE',
    }),
    getPendingVideos: () => request<any[]>('/admin/videos/pending'),
    auditVideo: (data: { videoId: string; status: string }) => request<any>('/admin/videos/audit', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
    deleteVideo: (id: string) => request<{ message: string }>(`/admin/videos/${id}`, {
      method: 'DELETE',
    }),
  },

  // 统计相关
  statistics: {
    getStudentScores: () => request<any>('/score/student'),
    getCourseAverageScore: (courseId: string) => request<any>(`/score/course/average/${courseId}`),
    getCourseScoreDistribution: (courseId: string) => request<any>(`/score/course/distribution/${courseId}`),
  },

  // 学生相关
  student: {
    getOverview: () => request<any>('/student/overview'),
    getRecentStudies: async () => {
      const response = await request<{
        success: boolean;
        data: any[];
      }>('/api/video-watch/recent');
      return response.data || [];
    },
    recordLearningTime: (chapterId: string, duration: number) => request<any>('/student/learning-time', {
      method: 'POST',
      body: JSON.stringify({ chapterId, duration }),
    }),
  },

  // 社区相关
  community: {
    getPosts: () => request<any[]>('/community/posts', {
      method: 'GET',
    }),
    getPostById: (postId: string) => request<any>(`/community/posts/${postId}`),
    createPost: (title: string, content: string, category: string, extras?: { imageUrls?: string; videoUrl?: string; documentUrls?: string; location?: string }) => request<any>('/community/posts', {
      method: 'POST',
      body: JSON.stringify({ title, content, category, ...extras }),
    }),
    deletePost: (postId: string) => request<{ message: string }>(`/community/posts/${postId}`, {
      method: 'DELETE',
    }),
    toggleTopPost: (postId: string, top: boolean) => request<{ message: string }>(`/community/posts/${postId}/top`, {
      method: 'PUT',
      body: JSON.stringify({ top }),
    }),
    getComments: (postId: string) => request<any[]>(`/community/posts/${postId}/comments`),
    createComment: (postId: string, content: string, parentId?: string) => request<any>(`/community/posts/${postId}/comments`, {
      method: 'POST',
      body: JSON.stringify({ content, parentId }),
    }),
    likePost: (postId: string) => request<any>(`/community/posts/${postId}/like`, {
      method: 'POST',
    }),
    collectPost: (postId: string) => request<any>(`/community/posts/${postId}/collect`, {
      method: 'POST',
    }),
    checkInteraction: (postId: string, type: string) => request<boolean>(`/community/posts/${postId}/interactions/check`, {
      method: 'GET',
      params: { type },
    }),
  },

  // 单词相关
  words: {
    getByLevel: (level: string) => request<any[]>(`/words/level/${level}`),
  },

  // 视频相关
  videos: {
    getAll: () => request<any[]>('/api/videos'),
    getByCategory: (category: string) => request<any[]>(`/api/videos/category/${category}`),
    getByCategoryAndType: (category: string, type: string) => request<any[]>(`/api/videos/category/${category}/type/${type}`),
    getTeacherVideos: () => request<any[]>('/api/videos/teacher'),
    delete: (id: number) => request<any>(`/api/videos/${id}`, {
      method: 'DELETE',
    }),
  },

  // 文件上传相关
  upload: {
    avatar: async (file: File) => {
      const formData = new FormData();
      formData.append('file', file);
      
      const token = localStorage.getItem('token');
      const headers: Record<string, string> = {};
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
      
      const response = await fetch(`${API_BASE_URL}/api/upload/avatar`, {
        method: 'POST',
        headers,
        body: formData,
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      return await response.json();
    },
    video: async (file: File, title: string, description: string, category: string, type: string) => {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('title', title);
      formData.append('description', description);
      formData.append('category', category);
      formData.append('type', type);
      
      const token = localStorage.getItem('token');
      const headers: Record<string, string> = {};
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
      
      const response = await fetch(`${API_BASE_URL}/api/upload/video`, {
        method: 'POST',
        headers,
        body: formData,
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      return await response.json();
    },
    image: async (file: File) => {
      const formData = new FormData();
      formData.append('file', file);
      
      const token = localStorage.getItem('token');
      const headers: Record<string, string> = {};
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
      
      const response = await fetch(`${API_BASE_URL}/api/upload/image`, {
        method: 'POST',
        headers,
        body: formData,
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      return await response.json();
    },
    audio: async (file: File) => {
      const formData = new FormData();
      formData.append('file', file);
      
      const token = localStorage.getItem('token');
      const headers: Record<string, string> = {};
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
      
      const response = await fetch(`${API_BASE_URL}/api/upload/image`, {
        method: 'POST',
        headers,
        body: formData,
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const result = await response.json();
      // 保持与图片上传相同的返回格式
      return {
        success: true,
        audioUrl: result.imageUrl
      };
    },
  },
  studyRecord: {
    getWeekly: async () => {
      return await request<{
        success: boolean;
        dates: string[];
        minutes: number[];
      }>('/api/study-record/weekly');
    },
    add: async (minutes: number) => {
      const params = new URLSearchParams();
      params.append('minutes', minutes.toString());
      
      return await request<{
        success: boolean;
        message: string;
      }>('/api/study-record/add', {
        method: 'POST',
        body: params,
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      });
    },
  },

  // 四级真题作业相关
  cet4Homework: {
    submit: (data: { homeworkId: number; studentAnswer: string; subjectiveAnswer?: string; subjectiveImage?: string }) =>
      request<any>('/api/cet4-homework/submit', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    getMySubmissions: () => request<any>('/api/cet4-homework/my-submissions'),
    getTeacherSubmissions: () => request<any>('/api/cet4-homework/teacher/submissions'),
    grade: (data: { submissionId: number; score: number; feedback?: string }) => request<any>('/api/cet4-homework/grade', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
    checkSubmitted: (homeworkId: number) => request<any>(`/api/cet4-homework/check-submitted/${homeworkId}`),
    getAnswer: (homeworkId: number) => request<any>(`/api/cet4-homework/answer/${homeworkId}`),
  },
};