import { useEffect, useState } from 'react'
import { Link, Navigate, Route, Routes, useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { api, getToken, saveToken } from './api.js'

function ErrorText({ error }) { return error ? <p className="error">{error.message}</p> : null }

export function Composer({ label = 'Post', onSubmit }) {
  const [text, setText] = useState('')
  async function submit(event) {
    event.preventDefault()
    if (!text.trim()) return
    await onSubmit(text.trim())
    setText('')
  }
  return <form onSubmit={submit} className="composer">
    <textarea aria-label="Text" maxLength={250} value={text} onChange={(e) => setText(e.target.value)} />
    <div><small>{text.length}/250</small> <button disabled={!text.trim()}>{label}</button></div>
  </form>
}

function PostCard({ post }) {
  return <article>
    <div><Link to={`/profile/${post.authorUsername}`}>@{post.authorUsername}</Link> · <time>{new Date(post.createdAt).toLocaleString()}</time></div>
    <p>{post.text}</p>
    <div><Link to={`/posts/${post.rootId ?? post.id}`}>{post.replyCount ?? 0} replies</Link> · {post.likeCount ?? 0} likes</div>
  </article>
}

function Nav({ me, logout }) {
  return <nav><Link to="/">timeline</Link><Link to="/search">search</Link>{me ? <><Link to={`/profile/${me.username}`}>@{me.username}</Link><button onClick={logout}>logout</button></> : <Link to="/login">login</Link>}</nav>
}

function AuthPage({ onAuth }) {
  const [registering, setRegistering] = useState(false)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(null)
  const navigate = useNavigate()
  async function submit(event) {
    event.preventDefault(); setError(null)
    try {
      const result = await (registering ? api.register({ username, password }) : api.login({ username, password }))
      saveToken(result.accessToken); await onAuth(); navigate('/')
    } catch (e) { setError(e) }
  }
  return <main><h1>{registering ? 'Create account' : 'Login'}</h1><form onSubmit={submit}>
    <label>Username<input value={username} pattern="[a-z0-9_]{3,30}" onChange={(e) => setUsername(e.target.value.toLowerCase())} /></label>
    <label>Password<input type="password" minLength={8} maxLength={72} value={password} onChange={(e) => setPassword(e.target.value)} /></label>
    <button>{registering ? 'Register' : 'Login'}</button>
  </form><ErrorText error={error} /><button className="link-button" onClick={() => setRegistering(!registering)}>{registering ? 'Use existing account' : 'Create an account'}</button></main>
}

function TimelinePage({ me }) {
  const [page, setPage] = useState({ items: [] }); const [error, setError] = useState(null)
  async function load() { try { setPage(await api.feed()) } catch (e) { setError(e) } }
  useEffect(() => { if (me) load() }, [me])
  if (!me) return <main><h1>Text Social</h1><p><Link to="/login">Log in</Link> to see your timeline.</p></main>
  return <main><h1>Timeline</h1><Composer onSubmit={async (text) => { await api.createPost(text); await load() }} /><ErrorText error={error} />{page.items.map((post) => <PostCard key={post.id} post={post} />)}</main>
}

function ReplyNode({ post }) {
  const [children, setChildren] = useState(null); const [error, setError] = useState(null)
  async function expand() { try { setChildren((await api.replies(post.id)).items) } catch (e) { setError(e) } }
  return <div className="reply"><PostCard post={post} />{children === null && <button onClick={expand}>show replies</button>}<ErrorText error={error} />{children?.map((child) => <ReplyNode key={child.id} post={child} />)}</div>
}

function ThreadPage() {
  const { id } = useParams(); const [post, setPost] = useState(null); const [replies, setReplies] = useState([]); const [error, setError] = useState(null)
  async function load() { try { setPost(await api.post(id)); setReplies((await api.replies(id)).items) } catch (e) { setError(e) } }
  useEffect(() => { load() }, [id])
  return <main><h1>Thread</h1><ErrorText error={error} />{post && <><PostCard post={post} /><Composer label="Reply" onSubmit={async (text) => { await api.reply(id, text); await load() }} />{replies.map((reply) => <ReplyNode key={reply.id} post={reply} />)}</>}</main>
}

function ProfilePage({ me }) {
  const { username } = useParams(); const [user, setUser] = useState(null); const [error, setError] = useState(null)
  async function load() { try { setUser(await api.user(username)) } catch (e) { setError(e) } }
  useEffect(() => { load() }, [username])
  return <main><h1>@{username}</h1><ErrorText error={error} />{user && <><p>{user.followerCount} followers</p>{me?.id !== user.id && <button onClick={async () => { await api.follow(user.id, user.followedByMe); await load() }}>{user.followedByMe ? 'Unfollow' : 'Follow'}</button>}</>}</main>
}

function SearchPage() {
  const [params, setParams] = useSearchParams(); const [query, setQuery] = useState(params.get('q') ?? ''); const [items, setItems] = useState([]); const [error, setError] = useState(null)
  async function submit(event) { event.preventDefault(); setParams({ q: query }); try { setItems((await api.search(query)).items) } catch (e) { setError(e) } }
  return <main><h1>Search</h1><form onSubmit={submit}><input aria-label="Search" minLength={2} maxLength={250} value={query} onChange={(e) => setQuery(e.target.value)} /><button>Search</button></form><ErrorText error={error} />{items.map((post) => <PostCard key={post.id} post={post} />)}</main>
}

export default function App() {
  const [me, setMe] = useState(null); const [loading, setLoading] = useState(Boolean(getToken()))
  async function refreshMe() { try { setMe(await api.me()) } catch { saveToken(null); setMe(null) } finally { setLoading(false) } }
  useEffect(() => { if (getToken()) refreshMe() }, [])
  if (loading) return <p>Loading…</p>
  return <div className="shell"><Nav me={me} logout={() => { saveToken(null); setMe(null) }} /><Routes>
    <Route path="/" element={<TimelinePage me={me} />} />
    <Route path="/login" element={me ? <Navigate to="/" /> : <AuthPage onAuth={refreshMe} />} />
    <Route path="/profile/:username" element={<ProfilePage me={me} />} />
    <Route path="/posts/:id" element={<ThreadPage />} />
    <Route path="/search" element={<SearchPage />} />
  </Routes></div>
}

