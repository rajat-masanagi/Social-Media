const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8911'

export function getToken() { return localStorage.getItem('accessToken') }
export function saveToken(token) { token ? localStorage.setItem('accessToken', token) : localStorage.removeItem('accessToken') }

async function request(path, options = {}) {
  const token = getToken()
  const isPublicAuth = path === '/api/auth/login' || path === '/api/auth/register'
  const headers = { ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...(!isPublicAuth && token ? { Authorization: `Bearer ${token}` } : {}), ...options.headers }
  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers,
  })
  if (response.status === 204) return null
  const body = await response.json().catch(() => ({}))
  if (!response.ok) {
    if (response.status === 401 && !isPublicAuth) saveToken(null)
    const error = new Error(body.message ?? `Request failed (${response.status})`)
    error.code = body.code
    error.status = response.status
    error.requestId = response.headers.get('X-Request-Id')
    throw error
  }
  return body
}

export const api = {
  register: (body) => request('/api/auth/register', { method: 'POST', body: JSON.stringify(body) }),
  login: (body) => request('/api/auth/login', { method: 'POST', body: JSON.stringify(body) }),
  me: () => request('/api/me'),
  feed: (cursor = '', limit = 20) => request(`/api/feed?limit=${limit}${cursor ? `&cursor=${encodeURIComponent(cursor)}` : ''}`),
  createPost: (text) => request('/api/posts', { method: 'POST', body: JSON.stringify({ text }) }),
  post: (id) => request(`/api/posts/${id}`),
  replies: (id, cursor = '') => request(`/api/posts/${id}/replies?limit=20${cursor ? `&cursor=${encodeURIComponent(cursor)}` : ''}`),
  reply: (id, text) => request(`/api/posts/${id}/replies`, { method: 'POST', body: JSON.stringify({ text }) }),
  like: (id, liked) => request(`/api/posts/${id}/likes/me`, { method: liked ? 'DELETE' : 'POST' }),
  user: (username) => request(`/api/users/${encodeURIComponent(username)}`),
  follow: (id, followed) => request(`/api/users/${id}/follow`, { method: followed ? 'DELETE' : 'POST' }),
  search: (query, cursor = '') => request(`/api/search?q=${encodeURIComponent(query)}&limit=20${cursor ? `&cursor=${encodeURIComponent(cursor)}` : ''}`),
}
