---
root: .components.layouts.BlogPostLayout("FreeFlarum")
title: FreeFlarum – Free & Fast Flarum Forum Hosting
author: Kevin Svitač
original_date_time: 18/03/2025 12:00
last_edited_date_time: 18/03/2025 12:00
---

[FreeFlarum](https://freeflarum.com) is a free and fast [Flarum](https://flarum.org/) forum hosting service. I am maintaining it since Aug 2019. It has hosted over 40,000 unique forums in its lifetime, and it is currently the home of 4,000+ active forums.

## Features

- 1-minute setup
- No ads or trackers
- Free (hosting is paid by voluntary donations)
- Automatic backups & monitoring
- [Fast community support](https://support.freeflarum.com)

## Technical Details

Hosted on [Hetzner CAX31](https://www.hetzner.com/dedicated-server/cax21) in Frankfurt, Germany:

- 8 vCPU (Ampere® Altra®)
- 16 GB RAM
- 160 GB NVME SSD (+ 100 GB drive for backups)

**Frontend:** Flask (Python) with Alpine.js for interactivity.
**Backend:** FastAPI (uvicorn) with MariaDB and Caddy as a reverse proxy. PHP-FPM to serve Flarum.
