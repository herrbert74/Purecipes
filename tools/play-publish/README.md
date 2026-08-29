# Play release upload

Uploads a signed Android App Bundle to a Google Play track via the Android Publisher API (`app.purecipes`).

Used by [`.github/workflows/distribute-android.yml`](../../.github/workflows/distribute-android.yml) on tag `v*` (closed testing / API track `alpha`), alongside Firebase App Distribution (same release AAB).

| Step | Command |
|------|---------|
| Build signed AAB | `./gradlew :app:bundleRelease` |
| Preview upload | `./gradlew :tools:play-publish:uploadPlayRelease -PdryRun` |
| Upload to Play | `./gradlew :tools:play-publish:uploadPlayRelease` |

Defaults: track **alpha** (closed testing), status **completed**, language **en-GB**, AAB at `app/build/outputs/bundle/release/app-release.aab`, R8 mapping at `app/build/outputs/mapping/release/mapping.txt`, notes from `build/release-notes.txt`.

Release builds set `ndk.debugSymbolLevel = SYMBOL_TABLE` so native debug symbols are embedded in the AAB for Play crash symbolication. The upload step also sends the R8 `mapping.txt` for Java/Kotlin deobfuscation. The Distribute Android workflow also archives the AAB and `mapping.txt` as a GitHub Actions artifact (`android-release-<version>`, 90-day retention) for manual download or Play Console re-upload.

## Credentials

Same Play Console service account JSON as screenshot uploads and Cursor’s play-store MCP.

Resolution order:

1. `--credentials /path/to/play-sa.json` (main class directly)
2. Gradle `-Ppurecipes.play.serviceAccountJson=/path/to/play-sa.json`
3. Environment variable `GOOGLE_APPLICATION_CREDENTIALS`

```bash
./gradlew :tools:play-publish:uploadPlayRelease \
  -Ppurecipes.play.serviceAccountJson=/path/to/play-sa.json \
  -Ppurecipes.play.releaseName=0.2.0
```

Never commit service account JSON or absolute machine paths into the repo.

### Play Console permissions

Beyond screenshot uploads, the service account needs release access on **Purecipes** (`app.purecipes`):

1. Enable **Google Play Android Developer API** on the Cloud project that owns the key.
2. Link that project in Play Console → **Setup → API access**.
3. Invite the service account under **Users and permissions**.
4. Grant app access with at least **View app information** and **Release to testing tracks** (add **Release to production** only when you automate production).
5. **Save twice** if Play Console requires it after changing app permissions. Wait a minute, then retry.

## Gradle properties

| Property | Default | Meaning |
|----------|---------|---------|
| `purecipes.play.aab` | `app/build/outputs/bundle/release/app-release.aab` | Path to the signed AAB |
| `purecipes.play.mapping` | `app/build/outputs/mapping/release/mapping.txt` | R8 mapping uploaded for Play deobfuscation |
| `purecipes.play.track` | `alpha` | `internal`, `alpha` (closed), `beta` (open), or `production` |
| `purecipes.play.status` | `completed` | `completed`, `draft`, `inProgress`, or `halted` |
| `purecipes.play.language` / `language` | `en-GB` | Release notes locale |
| `purecipes.play.releaseNotesFile` | `build/release-notes.txt` | Plain-text notes (must be ≤500 chars; upload fails if longer) |
| `purecipes.play.releaseName` | (omit) | Optional release name in Play Console |
| `dryRun` | off | Print plan only |
