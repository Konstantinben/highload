REM docker build -t kpolyanichko/hl-sonet-post-hw-12-amd64:latest .
docker buildx build --platform linux/amd64 -t kpolyanichko/hl-sonet-post-hw-12-amd64:latest --push .