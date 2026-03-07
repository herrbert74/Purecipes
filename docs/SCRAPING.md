# Scraping Recipes

This document explains the manual workflow for generating a list of recipe URLs and running the scraping script.

## Step 1: Generating a URL list

1. Open your browser and go to **https://simplescraper.io/extracturls**.
2. Enter the home page or category page of a recipe website you want to scrape (e.g., `https://example.com/recipes`).
3. Configure the tool to crawl whatever depth you need (usually a few levels deep is enough to capture all recipe pages).
4. Run the extraction and wait for the list of URLs to appear.
5. Copy the resulting URLs.
6. Paste the list into a plain text file and save it to your local machine. The convention used by the scripts is:
   ```
   ~/documents/recipes/<site-name>.txt
   ```
   for example:
   ```
   ~/documents/recipes/site-example.txt
   ```

## Step 2: Preparing to scrape

### Requirements

The scraper is a Kotlin script located at `scripts/recipe_site_scraper.main.kts` in this repository. It reads a file containing URLs and processes each one.

Make sure you have a working Kotlin CLI installation (`kotlinc`, `kotlin`) and that you can run `./gradlew` from the project root.

### Input format

- Each line in the input file should be a single URL.
- Empty lines and comments are ignored.

## Step 3: Running the scraping script

From the project root, you can invoke the script. Example:

```bash
cd scripts
kotlin scripts/recipe_site_scraper.main.kts --bonappetit ~/documents/output --urls-file ~/documents/recipes/bonappetit.txt
```

The script will iterate over each URL, fetch the page, parse it according to the site's configuration, and emit JSON output containing the scraped recipe data.

## Step 4: Post-scraping

- The output JSON file is written to the path specified by `--output`.
- You can later import this data into the backend or process it further.

## Notes from the script

- The script automatically adds a timestamp and logs progress to the console.
- It handles basic retries and rate limiting to avoid overloading recipe sites.
- If a site requires custom parsing logic, there may be a configuration file or additional Kotlin code under `scripts/` or the backend module. Check the code comments for `SiteScraperMain` or related classes.

## Troubleshooting

- Ensure the input file uses LF line endings.
- If you encounter SSL or network issues, check your local firewall or VPN.
- You can run the script with `--help` to see all available options.

---

By following these steps, you can generate a list of recipe URLs manually and feed them into the scraper to harvest recipe data.