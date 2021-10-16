# Run Application using docker-compose (standalone)

---

## Install prerequisites
- `docker-compose`
- `docker.io`
```shell
# For Debian
sudo apt install -y \
  docker.io \
  docker-compose 
```

Update `docker-compose.yml` if needed, such as PASSWORD environment variables.

Then run the application along with the database by executing:
```shell
docker-compose -f docker-compose.yml up
```
Volume does not mount to host path by default, therefore data will be erased when the database
container is killed.

## Verify deployment

### Create new shorten URLs

```shell
curl --location --request POST 'http://localhost:8080/' \
--header 'Content-Type: application/json' \
--data-raw '{
  "url": "https://youtube.com"
}'
```

### Get all existing shorten URLs

```shell
curl --location --request GET 'http://localhost:8080'
```

### Use shorten URL for redirect
```shell
curl --location --request GET 'http://localhost:8080/<shortenUrl>'
```

### Get HTTP response code
HTTP response code should be `304 NOT_MODIFIED`
```shell
curl -o /dev/null -s -w "%{http_code}\n" 'http://localhost:8080/<shortenUrl>'
```