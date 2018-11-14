# play-vs-spring
Comparing play framework and spring boot

# Test

Create a post:

```
curl -X POST -H 'Content-Type: application/json' -d '{"title": "test 1", "body": "this is my first post"}' http://localhost:8080/v1/posts
```

Get a post:

```
curl http://localhost:8080/v1/posts/{id}
```
