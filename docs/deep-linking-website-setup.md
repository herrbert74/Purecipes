# Deep linking: website and local testing

Canonical share URLs are always:

- `https://purecipes.app/r/{recipeId}`
- `https://purecipes.app/c/{cookbookShareToken}` (UUID; opens import flow in the app, not a raw cookbook database id)

With **Universal Links (iOS)** and **App Links (Android)** configured, the OS opens the native app directly when the user taps those HTTPS links. No HTML redirect page is required in the app or on the server for that flow.

The Wasm web app can later serve the same paths when the app is not installed.

## App identifiers in this repo

| Build | Android package (`applicationId`) | iOS bundle ID |
|-------|-----------------------------------|---------------|
| **Release** | `app.purecipes` | `app.purecipes.PurecipesIOSApp` |
| **Debug** | `app.purecipes.debug` | `app.purecipes.PurecipesIOSApp` (same as release) |
| **Staging** | `app.purecipes.staging` | `app.purecipes.PurecipesIOSApp` (same as release) |

Android debug/staging use an `applicationIdSuffix` in `app/build.gradle.kts`. iOS uses one bundle identifier for all Xcode configurations in this project, so **one** Universal Links entry in `apple-app-site-association` covers debug and release installs.

`Info.plist` contains Facebook/Google client IDs and `CFBundleIdentifier`; it does **not** contain your Apple Team ID.

## Apple Team ID (for iOS only — not Google)

The value in `appID` (`TEAM_ID.app.purecipes.PurecipesIOSApp`) is your **Apple Developer Team ID**, not anything from Google or Firebase.

**Where to find it:**

1. [Apple Developer](https://developer.apple.com/account) → **Membership** → **Team ID** (10 characters, e.g. `ABCDE12345`).
2. **Xcode** → open `iosApp/PurecipesIOSApp/PurecipesIOSApp.xcodeproj` → select the app target → **Signing & Capabilities** → select your **Team** → open the team in [developer.apple.com](https://developer.apple.com/account) or hover the team name in Xcode’s signing UI (Team ID is shown there on recent Xcode versions).
3. **Keychain / provisioning profile** metadata sometimes lists the team, but the Developer account page is the authoritative source.

This is unrelated to `GIDClientID`, `PURECIPES_GOOGLE_*`, or `FacebookAppID` in `Info.plist`.

Skip iOS setup until you have an Apple Developer account. You can deploy `assetlinks.json` only; leave `PURECIPES_IOS_TEAM_ID` unset and ignore `apple-app-site-association` for now (the backend returns 404 for AASA without it, which is fine).

## Android release: two SHA-256 fingerprints (Firebase / Play)

If Firebase shows **two** SHA-256 certificates for release, that is normal when **Google Play App Signing** is enabled (default for new apps):

| Certificate | Who holds the key | Typical install source |
|---------------|-------------------|-------------------------|
| **Upload certificate** | You (your release/upload keystore) | Firebase App Distribution, local release APK, sideload, internal testing you sign yourself |
| **App signing certificate** | Google | Play Store (production and most internal/closed tracks after Google re-signs) |

You sign the AAB/APK with your **upload key**; Google re-signs with the **app signing key** before users download from Play. App Links verification checks the certificate on the **installed** APK, so both fingerprints must appear on the **same** `app.purecipes` entry.

**What to do:** put **both** SHA-256 values in one array (comma-separated in env, or two strings in JSON):

```json
"sha256_cert_fingerprints": [
  "UPLOAD_OR_UPLOAD_KEY_SHA256",
  "PLAY_APP_SIGNING_SHA256"
]
```

**Where to copy them (authoritative):**

1. [Google Play Console](https://play.google.com/console) → your app → **Test and release** → **App integrity** (or **Setup** → **App signing**).
2. **App signing key certificate** → SHA-256 (for Play Store installs).
3. **Upload key certificate** → SHA-256 (for builds you sign and upload / Firebase).

Firebase project settings often list the same certificates; they must match Play Console, not a different local keystore by mistake.

**Do not** put the debug keystore fingerprint in the `app.purecipes` entry. That belongs only on `app.purecipes.debug` (see below).

**VPS:** add the same values to `/etc/purecipes-backend.env` (see Option A §1), then restart the backend service.

## What you need on `purecipes.app`

| Requirement | Purpose |
|-------------|---------|
| DNS pointing to your server | Hostname resolves |
| Valid **HTTPS** (TLS certificate) | Required for App Links / Universal Links |
| `/.well-known/assetlinks.json` | Android verification (release **and** debug entries) |
| `/.well-known/apple-app-site-association` | iOS verification (optional until Apple Developer is set up) |

Optional later: static Wasm build at `/` so `/r/*` and `/c/*` work in the browser without the app.

Your Ktor backend serves `assetlinks.json` when at least one Android fingerprint env var is set. `apple-app-site-association` is served only when `PURECIPES_IOS_TEAM_ID` is set.

The JSON must use **snake_case** keys per [Digital Asset Links](https://developers.google.com/digital-asset-links/v1/statements): `package_name`, `sha256_cert_fingerprints` (not `packageName` / `sha256CertFingerprints`). Fingerprints must be **uppercase** hex with colons (e.g. `18:0D:AE:62:…`); lowercase values cause `ERROR_CODE_MALFORMED_CONTENT` / Android `1024`. The backend uppercases env values automatically.

Check what Google sees:

```bash
curl -sS "https://digitalassetlinks.googleapis.com/v1/statements:list?source.web.site=https://purecipes.app&relation=delegate_permission/common.handle_all_urls"
```

When healthy, `statements` is non-empty and `errorCode` is absent. After fixes: redeploy/restart backend, then `adb shell pm verify-app-links --re-verify …` (reinstall the app if the device still shows `1024`).

## Option A — Proxy well-known paths to the backend

If `purecipes.app` terminates HTTPS on nginx/Caddy and your API runs locally (e.g. `http://127.0.0.1:8080`):

### 1. Environment variables on the backend (runtime only, not Gradle build)

The backend reads these with `System.getenv` when serving `/.well-known/*`. They are **not** baked into `:backend:shadowJar`.

On the VPS you already use **`/etc/purecipes-backend.env`** (loaded by systemd for the backend service). You only need to **append** the lines below and **restart** that service. Do not create `/etc/purecipes/backend.env` or a new systemd drop-in unless your unit does not reference the existing file yet.

**Append to `/etc/purecipes-backend.env`** (values from `~/Documents/purecipes/assetlinks.json`):

```bash
PURECIPES_ANDROID_RELEASE_SHA256_CERT_FINGERPRINTS=5f:e4:c3:4e:6e:8e:36:02:ee:98:11:c7:96:d3:90:4c:8c:0e:71:16:b7:ae:ad:67:27:92:f7:1f:77:49:e0:48,99:16:ba:24:d1:02:8c:3b:44:33:c8:de:97:b4:9a:93:f7:16:9a:22:26:a5:f4:e0:ce:1d:aa:dd:c7:23:3b:8e
PURECIPES_ANDROID_DEBUG_SHA256_CERT_FINGERPRINTS=18:0d:ae:62:87:2e:e9:0a:a0:18:fd:76:23:eb:3e:37:62:94:78:c3:3a:57:b2:4e:de:51:a0:0d:00:4d:4b:53
```

**On the VPS (SSH):**

```bash
sudo nano /etc/purecipes-backend.env
# paste the two lines above at the end, save

sudo grep PURECIPES /etc/purecipes-backend.env
sudo systemctl restart purecipes-backend
sudo systemctl status purecipes-backend --no-pager

curl -sS -o /dev/null -w '%{http_code}\n' http://127.0.0.1:8080/.well-known/assetlinks.json
curl -sS http://127.0.0.1:8080/.well-known/assetlinks.json
```

If `restart` fails with “unit not found”, find the real name: `systemctl list-units --type=service | grep -i pure` and restart that unit instead.

**From your Mac (public URL via nginx):**

```bash
curl -sS https://purecipes.app/.well-known/assetlinks.json
```

**Confirm systemd already loads the file** (only if something does not work):

```bash
systemctl cat purecipes-backend | grep -i EnvironmentFile
```

You should see `EnvironmentFile=/etc/purecipes-backend.env` (or `-`). If it is already there, no systemd edits are required.

**List variables:**

```bash
sudo grep PURECIPES /etc/purecipes-backend.env
PID=$(pgrep -f 'backend\.jar' | head -1)
sudo tr '\0' '\n' < /proc/$PID/environ | grep PURECIPES
```

**iOS** (optional): add `PURECIPES_IOS_TEAM_ID=...` to the same file when you have Apple Developer.

Legacy release env alias: `PURECIPES_ANDROID_SHA256_CERT_FINGERPRINTS` (same as release).

**Android — staging** (`app.purecipes.staging`, optional): `PURECIPES_ANDROID_STAGING_SHA256_CERT_FINGERPRINTS`

Fingerprints come from the **keystore that signs that build** (or copy from `assetlinks.json` as above):

```bash
# Release keystore (from app/build.gradle.kts signingConfigs.release)
keytool -list -v -keystore /path/to/release.keystore -alias YOUR_RELEASE_ALIAS | grep "SHA256"

# Debug: Android Studio default debug keystore (typical path on macOS)
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android | grep "SHA256"
```

Use the line labeled `SHA256:` (colons are fine in `assetlinks.json`).

### 2. nginx — edit `/etc/nginx/sites-available/purecipes`

The live site config is **`/etc/nginx/sites-available/purecipes`** (usually symlinked from `sites-enabled`). `nginx -T | grep well-known` returns nothing until you add the block below.

**1. See the current file**

```bash
sudo cat /etc/nginx/sites-available/purecipes
ls -l /etc/nginx/sites-enabled/purecipes
```

**2. Note the backend port** (default `8080`):

```bash
grep PURECIPES_BACKEND_PORT /etc/purecipes-backend.env
```

Use that port in `proxy_pass` below if it is not `8080`.

**3. Edit the file**

```bash
sudo nano /etc/nginx/sites-available/purecipes
```

Inside each `server { ... }` block that serves `purecipes.app` / `www.purecipes.app` on **port 443**, add this **above** any `location /` that uses `root` or `try_files` (so it is not swallowed by `index.html`):

```nginx
    location /.well-known/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
```

**Example** — before vs after (your paths may differ; keep your existing `ssl_certificate`, `location /api/`, etc.):

```nginx
server {
    listen 443 ssl;
    server_name purecipes.app www.purecipes.app;

    # ... your ssl_certificate lines ...

    location /.well-known/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
    }

    location / {
        root /var/www/purecipes;
        try_files $uri $uri/ /index.html;
    }
}
```

If you already proxy API traffic (e.g. `location /` or `/api/` → `127.0.0.1:8080`), still add the **`/.well-known/`** block: Android requires that exact path on the **same host** as the HTTPS links.

**4. Test and reload**

```bash
sudo nginx -t
sudo systemctl reload nginx
sudo nginx -T 2>/dev/null | grep -A4 'well-known'
```

**5. Verify**

```bash
curl -sS -D - https://purecipes.app/.well-known/assetlinks.json | head -20
curl -sS https://purecipes.app/.well-known/assetlinks.json | head -c 120
```

Body must start with `[`, not `<html`.

### 2b. DNS on Ionos + TLS on the VPS (not the Ionos “free certificate” button)

App Links need `https://purecipes.app` to hit **this VPS nginx**, not Ionos MyWebsite/WordPress. In Ionos **DNS**, set **A** records for `@` and `www` to the VPS public IP. The certificate installed in the Ionos admin panel only covers Ionos hosting; use **certbot on the VPS** once DNS points here.

**Certbot error:** `Could not automatically find a matching server block for purecipes.app` — nginx has no `server_name` matching the domain. Fix the config file first:

```bash
sudo grep -n server_name /etc/nginx/sites-available/purecipes
sudo nginx -T 2>/dev/null | grep server_name
```

Add or fix blocks so **both** names exist. Minimal file to pass certbot (adjust `root` if you use another path):

```nginx
server {
    listen 80;
    listen [::]:80;
    server_name purecipes.app www.purecipes.app;

    location /.well-known/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
    }

    location / {
        return 200 'ok';
        add_header Content-Type text/plain;
    }
}
```

```bash
sudo ln -sf /etc/nginx/sites-available/purecipes /etc/nginx/sites-enabled/purecipes
sudo nginx -t
sudo systemctl reload nginx
sudo certbot --nginx -d purecipes.app -d www.purecipes.app
```

Certbot will add `listen 443 ssl` and certificate paths. Keep the `location /.well-known/` block in the **443** server after certbot runs (certbot often edits the same file — open it and confirm the proxy block is still there).

If port 80 is blocked, use DNS challenge: `sudo certbot certonly --manual --preferred-challenges dns -d purecipes.app -d www.purecipes.app` and add the TXT record Ionos shows.

**After certbot succeeds but `curl https://purecipes.app/.well-known/assetlinks.json` fails:**

```bash
# A) Backend still OK?
curl -sS -w '\nHTTP %{http_code}\n' http://127.0.0.1:8080/.well-known/assetlinks.json | head -c 200

# B) HTTPS — status + first bytes (do not pipe huge HTML into head)
curl -sS -D /tmp/h.txt -o /tmp/body.txt https://purecipes.app/.well-known/assetlinks.json
grep -iE '^(HTTP|content-type|server):' /tmp/h.txt
head -c 120 /tmp/body.txt; echo

# C) Is /.well-known/ proxied on port 443?
sudo nginx -T 2>/dev/null | grep -A20 'server_name purecipes.app' | grep -A6 'well-known\|listen 443'
```

| Result | Fix |
|--------|-----|
| A = JSON, B = HTML or 404 | Add `location /.well-known/` to the **`listen 443 ssl`** `server` for `purecipes.app` (certbot often only has it on port 80 or removed it). |
| A = 404 | `sudo grep PURECIPES /etc/purecipes-backend.env` then `sudo systemctl restart purecipes-backend` |
| B = connection/SSL error | DNS or cert path; `sudo certbot certificates` |
| B = 301 to `www` | Test `curl ... https://www.purecipes.app/.well-known/assetlinks.json` or add the block on the `www` server too |

Re-add on the **443** server block (same backend port as in `/etc/purecipes-backend.env`):

```nginx
    location /.well-known/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
```

```bash
sudo nginx -t && sudo systemctl reload nginx
```

### 3. Verify

```bash
curl -sS -D - https://purecipes.app/.well-known/assetlinks.json -o /tmp/assetlinks.out
head -5 /tmp/assetlinks.out
file /tmp/assetlinks.out
```

`assetlinks.json` must return **200**, `Content-Type: application/json`, and body starting with `[` (not `<!DOCTYPE` or `<html`). Check that the release entry lists **both** release fingerprints and a separate debug entry if you use debug builds.

### Troubleshooting: curl shows HTML instead of JSON

**Step 1 — backend on the VPS (bypass nginx):**

```bash
grep PURECIPES /etc/purecipes-backend.env
curl -sS -D - http://127.0.0.1:8080/.well-known/assetlinks.json -o /tmp/out
head -3 /tmp/out
```

- Body starts with `[` → backend is fine; fix **nginx** (step 2).
- **404** or empty → env vars missing or backend not restarted; fix `/etc/purecipes-backend.env` and `sudo systemctl restart purecipes-backend`.
- Connection refused → wrong port: `grep PURECIPES_BACKEND_PORT /etc/purecipes-backend.env` and use that port in curl and in nginx `proxy_pass`.

**Step 2 — nginx must proxy `/.well-known/` before SPA `try_files`:**

Without a dedicated `location /.well-known/ { proxy_pass ... }`, requests often hit `try_files ... /index.html` and return your site’s HTML.

Edit **`/etc/nginx/sites-available/purecipes`** as in §2 above (`sudo nano`, add `location /.well-known/`, then `sudo nginx -t && sudo systemctl reload nginx`).

**Option B conflict:** if a static file exists at `/var/www/.../.well-known/assetlinks.json` and nginx serves the filesystem first, remove it or stop using Option B for that path so the backend (Option A) can answer.

### 4. Android: re-verify App Links

After deploying, for each installed package:

```bash
adb shell pm verify-app-links --re-verify app.purecipes
adb shell pm get-app-links app.purecipes

adb shell pm verify-app-links --re-verify app.purecipes.debug
adb shell pm get-app-links app.purecipes.debug
```

### 5. iOS

Reinstall the app after AASA is live. Apple caches association files; changes can take time to propagate.

---

## Option B — Static files on the website

Upload under the site document root at the exact paths below.

### `/.well-known/assetlinks.json`

Two targets (release + debug). Replace fingerprints with yours; add a third block for staging if you use that build.

```json
[
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "app.purecipes",
      "sha256_cert_fingerprints": [
        "UPLOAD_KEY_SHA256_FROM_PLAY_CONSOLE",
        "APP_SIGNING_KEY_SHA256_FROM_PLAY_CONSOLE"
      ]
    }
  },
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "app.purecipes.debug",
      "sha256_cert_fingerprints": ["DEBUG_KEYSTORE_SHA256_ONLY"]
    }
  }
]
```

Optional staging entry:

```json
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "app.purecipes.staging",
      "sha256_cert_fingerprints": ["STAGING_SHA256_FINGERPRINT"]
    }
  }
```

Each `package_name` must match the installed APK. A debug fingerprint on the release package (or the reverse) will **not** verify.

### `/.well-known/apple-app-site-association`

Replace `YOUR_TEAM_ID` with your Apple Team ID. Filename must **not** be `apple-app-site-association.json`.

Because debug and release share `app.purecipes.PurecipesIOSApp`, one `appID` is enough:

```json
{
  "applinks": {
    "apps": [],
    "details": [
      {
        "appID": "YOUR_TEAM_ID.app.purecipes.PurecipesIOSApp",
        "paths": ["/r/*", "/c/*"]
      }
    ]
  }
}
```

Serve with `Content-Type: application/json`. Do not redirect these URLs.

---

## Web fallback (when the app is not installed)

Not required for Universal/App Links to open the app. When you deploy the Wasm app, host it at `https://purecipes.app/` with SPA routing so `/r/123` loads the same client as `/`. The in-app Wasm build already reads `window.location` and navigates via `PublishWebLaunchLinkUseCase`.

Until Wasm is deployed, taps from users **without** the app may show a blank or 404 page in the browser; that is expected.

---

## Local testing (no website)

Use **adb** (Android) or **simctl** (iOS). Custom scheme and HTTPS are both handled by the app.

### Android debug (`app.purecipes.debug`)

```bash
adb shell am start -a android.intent.action.VIEW \
  -d "https://purecipes.app/r/RECIPE_ID" app.purecipes.debug

adb shell am start -a android.intent.action.VIEW \
  -d "purecipes://r/RECIPE_ID" app.purecipes.debug
```

Cookbook:

```bash
adb shell am start -a android.intent.action.VIEW \
  -d "https://purecipes.app/c/COOKBOOK_ID" app.purecipes.debug
```

### Android release (`app.purecipes`)

```bash
adb shell am start -a android.intent.action.VIEW \
  -d "https://purecipes.app/r/RECIPE_ID" app.purecipes

adb shell am start -a android.intent.action.VIEW \
  -d "purecipes://r/RECIPE_ID" app.purecipes
```

### iOS Simulator

Same bundle ID for debug and release builds:

```bash
xcrun simctl openurl booted "https://purecipes.app/r/RECIPE_ID"
xcrun simctl openurl booted "purecipes://r/RECIPE_ID"
```

Replace `RECIPE_ID` / `COOKBOOK_ID` with real integers from your database.

HTTPS links open the app automatically only after Universal/App Links are verified on the device. Until then, `purecipes://` via adb/simctl is the reliable local test.
