# lunch parser

## Command line usage
`boot [ run "raggmunk" ]`

## Build docker
`docker build -t lunch-parser:0.1 .`

## Run with jwilder/nginx-proxy
`docker run -d -p 3000 -e VIRTUAL_HOST=hostname -e VIRTUAL_PORT=3000 --name lunch-parser lunch-parser:0.1 boot start-server`

## Developing slack api
Use the npm package `http-echo-server` to verify the async queries. Supply the correct `response_url` in the post request and `text` with the query