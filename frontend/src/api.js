const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

export function getToken() { return localStorage.getItem('accessToken') }
export function saveToken(token) { token ? localStorage.setItem('accessToken', token) : localStorage.removeItem('accessToken') }

async function request(path, options = {}) {
  const token = getToken()
  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}), ...options.headers },
  })
  if (response.status === 204) return null
  const body = await response.json().catch(() => ({}))
  if (!response.ok) throw new Error(body.message ?? `Request failed (${response.status})`)
  return body
}

export const api = {
  register: (body) => request('/api/auth/register', { method: 'POST', body: JSON.stringify(body) }),
  login: (body) => request('/api/auth/login', { method: 'POST', body: JSON.stringify(body) }),
  me: () => request('/api/me'),
  feed: (cursor = '') => request(`/api/feed${cursor ? `?cursor=${encodeURIComponent(cursor)}` : ''}`),
  createPost: (text) => request('/api/posts', { method: 'POST', body: JSON.stringify({ text }) }),
  post: (id) => request(`/api/posts/${id}`),
  replies: (id) => request(`/api/posts/${id}/replies`),
  reply: (id, text) => request(`/api/posts/${id}/replies`, { method: 'POST', body: JSON.stringify({ text }) }),
  like: (id, liked) => request(`/api/posts/${id}/likes/me`, { method: liked ? 'DELETE' : 'POST' }),
  user: (username) => request(`/api/users/${encodeURIComponent(username)}`),
  follow: (id, followed) => request(`/api/users/${id}/follow`, { method: followed ? 'DELETE' : 'POST' }),
  search: (query) => request(`/api/search?q=${encodeURIComponent(query)}`),
}

