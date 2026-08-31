param([string]$BaseUrl = 'http://localhost:8911')
$ErrorActionPreference = 'Stop'
$runId = Get-Date -Format 'MMddHHmmss'
$username = "smoke_$runId"
$password = 'password123'
$results = [System.Collections.Generic.List[object]]::new()

function Record([string]$Name, [bool]$Passed, [string]$Detail) {
    $results.Add([pscustomobject]@{ Test = $Name; Passed = $Passed; Detail = $Detail })
    if (-not $Passed) { throw "$Name failed: $Detail" }
}
function Call([string]$Method, [string]$Path, $Body = $null, [string]$Token = '') {
    $headers = @{}
    if ($Token) { $headers.Authorization = "Bearer $Token" }
    $args = @{ Uri = "$BaseUrl$Path"; Method = $Method; Headers = $headers; UseBasicParsing = $true }
    if ($null -ne $Body) { $args.ContentType = 'application/json'; $args.Body = ($Body | ConvertTo-Json -Compress) }
    try { Invoke-WebRequest @args }
    catch {
        $response = $_.Exception.Response
        if ($null -eq $response) { throw }
        $reader = New-Object System.IO.StreamReader($response.GetResponseStream())
        try { $content = $reader.ReadToEnd() } finally { $reader.Dispose() }
        [pscustomobject]@{ StatusCode = [int]$response.StatusCode; Content = $content; Headers = $response.Headers }
    }
}

try {
    $preflight = Invoke-WebRequest -Uri "$BaseUrl/api/auth/login" -Method Options -UseBasicParsing -Headers @{
        Origin = 'http://localhost:8915'; 'Access-Control-Request-Method' = 'POST'; 'Access-Control-Request-Headers' = 'content-type'
    }
    Record 'CORS preflight' ($preflight.StatusCode -eq 200 -and $preflight.Headers['Access-Control-Allow-Origin'] -eq 'http://localhost:8915') "HTTP $($preflight.StatusCode)"

    $register = Call POST '/api/auth/register' @{ username = $username; password = $password }
    Record 'Register' ($register.StatusCode -eq 200) "HTTP $($register.StatusCode)"
    $auth = $register.Content | ConvertFrom-Json

    $duplicate = Call POST '/api/auth/register' @{ username = $username.ToUpperInvariant(); password = $password }
    Record 'Duplicate username' ($duplicate.StatusCode -eq 409) "HTTP $($duplicate.StatusCode)"

    $badLogin = Call POST '/api/auth/login' @{ username = $username; password = 'incorrect-password' }
    Record 'Bad login' ($badLogin.StatusCode -eq 401) "HTTP $($badLogin.StatusCode)"

    $me = Call GET '/api/me' $null $auth.accessToken
    Record 'Authenticated me' ($me.StatusCode -eq 200) "HTTP $($me.StatusCode)"

    $post = Call POST '/api/posts' @{ text = "Smoke test $runId" } $auth.accessToken
    Record 'Create post' ($post.StatusCode -eq 200) "HTTP $($post.StatusCode)"

    $profile = Call GET "/api/users/$username"
    Record 'Public profile' ($profile.StatusCode -eq 200) "HTTP $($profile.StatusCode)"

    $postId = ($post.Content | ConvertFrom-Json).id
    $reply = Call POST "/api/posts/$postId/replies" @{ text = 'Smoke reply' } $auth.accessToken
    Record 'Create reply' ($reply.StatusCode -eq 200) "HTTP $($reply.StatusCode)"
    $children = Call GET "/api/posts/$postId/replies?limit=20"
    Record 'Read replies' ($children.StatusCode -eq 200) "HTTP $($children.StatusCode)"

    $like = Call POST "/api/posts/$postId/likes/me" $null $auth.accessToken
    Record 'Like post' ($like.StatusCode -eq 204) "HTTP $($like.StatusCode)"
    $unlike = Call DELETE "/api/posts/$postId/likes/me" $null $auth.accessToken
    Record 'Unlike post' ($unlike.StatusCode -eq 204) "HTTP $($unlike.StatusCode)"

    $badCursor = Call GET "/api/posts/$postId/replies?cursor=not-a-valid-cursor" $null $auth.accessToken
    Record 'Malformed cursor rejected' ($badCursor.StatusCode -eq 400) "HTTP $($badCursor.StatusCode)"

    $search = Call GET "/api/search?q=$runId&limit=20"
    Record 'Search endpoint' ($search.StatusCode -eq 200) "HTTP $($search.StatusCode)"

    $feed = Call GET '/api/feed?limit=20' $null $auth.accessToken
    Record 'Feed endpoint' ($feed.StatusCode -eq 200) "HTTP $($feed.StatusCode)"

    $withoutToken = Call GET '/api/me'
    Record 'Missing token rejected' ($withoutToken.StatusCode -eq 401) "HTTP $($withoutToken.StatusCode)"
}
finally {
    $results | Format-Table -AutoSize
}
