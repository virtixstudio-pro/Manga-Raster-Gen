#!/bin/bash
git init
git branch -M main
git add .
git commit -m "feat: initial commit for Manga-Raster-Gen APK with GitHub Actions"
gh repo create Manga-Raster-Gen --public --source=. --remote=origin --push
