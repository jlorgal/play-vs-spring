# play-vs-spring
Comparing play framework (in java and scala) and spring webflux.

This test implements an API REST with 2 endpoints:
 - A POST resource to store a document in mongoDB.
 - A GET resource to query a document from mongoDB.

There is no tuning in any of the platforms. It was applied the default configuration.

**Summary performance** (with `wrk -c 1000 -t 8`):

| Framework | TPS (create) | TPS (get) |
| --------- | ------------ | --------- |
| Play java | 5224.71 | 5806.56 |
| Play scala | 370.42 | 3405.79 |
| Spring webflux | 6572.11 | 6528.66 |

**NOTE**: The mongoDB connection pool is 10 connections in play, but 100 connections in spring. It makes sense that the connection pool is large because we are working with 1000 concurrent connections.

# Test

Create a post:

```
curl -X POST -H 'Content-Type: application/json' \
     -d '{"title": "test 1", "body": "this is my first post"}' \
     http://localhost:8080/v1/posts
```

Get a post:

```
curl http://localhost:8080/v1/posts/{id}
```

# Performance comparison

## Play java

### Create a post

```
$ wrk -c 500 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    72.93ms   33.54ms 571.29ms   72.52%
    Req/Sec   842.67    306.72     1.96k    69.62%
  100609 requests in 30.07s, 22.07MB read
  Socket errors: connect 253, read 156, write 24, timeout 0
Requests/sec:   3345.71
Transfer/sec:    751.48KB

$ wrk -c 500 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    46.61ms   18.82ms 290.10ms   76.20%
    Req/Sec   742.90    558.29     2.01k    46.00%
  152735 requests in 30.10s, 33.50MB read
  Socket errors: connect 253, read 168, write 76, timeout 0
Requests/sec:   5074.83
Transfer/sec:      1.11MB

$ wrk -c 1000 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    50.95ms   20.73ms 245.63ms   76.38%
    Req/Sec     1.21k     1.10k    3.13k    70.18%
  143870 requests in 30.07s, 31.56MB read
  Socket errors: connect 753, read 153, write 30, timeout 0
Requests/sec:   4783.94
Transfer/sec:      1.05MB

$ wrk -c 1000 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    46.36ms   17.29ms 214.93ms   74.50%
    Req/Sec     0.88k   656.59     2.56k    66.83%
  157035 requests in 30.06s, 34.44MB read
  Socket errors: connect 757, read 106, write 39, timeout 0
Requests/sec:   5224.71
Transfer/sec:      1.15MB
```

### Get a post

```
$ wrk -c 500 -t 4 -d 30s http://localhost:8080/v1/posts/5bed4bcaa4b79e101884c441
Running 30s test @ http://localhost:8080/v1/posts/5bed4bcaa4b79e101884c441
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    68.76ms   54.27ms   1.27s    97.33%
    Req/Sec     0.94k   527.61     1.88k    59.87%
  111219 requests in 30.07s, 24.93MB read
  Socket errors: connect 253, read 171, write 5, timeout 0
Requests/sec:   3699.28
Transfer/sec:    848.95KB

$ wrk -c 500 -t 8 -d 30s http://localhost:8080/v1/posts/5bed4bcaa4b79e101884c441
Running 30s test @ http://localhost:8080/v1/posts/5bed4bcaa4b79e101884c441
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    42.40ms   16.85ms 550.21ms   91.80%
    Req/Sec     1.15k   468.72     3.78k    73.69%
  170808 requests in 30.08s, 38.28MB read
  Socket errors: connect 253, read 201, write 8, timeout 0
Requests/sec:   5678.62
Transfer/sec:      1.27MB

$ wrk -c 1000 -t 4 -d 30s http://localhost:8080/v1/posts/5bed4bcaa4b79e101884c441
Running 30s test @ http://localhost:8080/v1/posts/5bed4bcaa4b79e101884c441
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    44.94ms   17.33ms 726.64ms   88.98%
    Req/Sec     1.37k     1.19k    3.21k    49.83%
  162444 requests in 30.10s, 36.41MB read
  Socket errors: connect 753, read 201, write 50, timeout 0
Requests/sec:   5396.70
Transfer/sec:      1.21MB

$ wrk -c 1000 -t 8 -d 30s http://localhost:8080/v1/posts/5bed4bcaa4b79e101884c441
Running 30s test @ http://localhost:8080/v1/posts/5bed4bcaa4b79e101884c441
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    41.15ms   15.73ms 693.29ms   90.35%
    Req/Sec     1.18k     1.28k    3.33k    67.05%
  174729 requests in 30.09s, 39.16MB read
  Socket errors: connect 757, read 206, write 12, timeout 0
Requests/sec:   5806.56
Transfer/sec:      1.30MB
```

## Play scala

### Create a post

```
$ wrk -c 500 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   679.69ms  427.17ms   1.97s    61.31%
    Req/Sec    85.99     33.57   232.00     66.04%
  10185 requests in 30.07s, 4.34MB read
  Socket errors: connect 253, read 342, write 26, timeout 21
Requests/sec:    338.76
Transfer/sec:    147.88KB

$ wrk -c 500 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   625.06ms  258.31ms   1.34s    68.72%
    Req/Sec    77.08     36.36   260.00     60.80%
  11424 requests in 30.09s, 4.87MB read
  Socket errors: connect 253, read 177, write 63, timeout 0
Requests/sec:    379.65
Transfer/sec:    165.72KB

$ wrk -c 1000 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   607.84ms  329.37ms   1.74s    66.77%
    Req/Sec    98.85     72.19   310.00     63.23%
  11438 requests in 30.07s, 4.88MB read
  Socket errors: connect 753, read 266, write 41, timeout 0
Requests/sec:    380.43
Transfer/sec:    166.06KB

$ wrk -c 1000 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   615.83ms  322.51ms   2.00s    67.20%
    Req/Sec    64.28     40.17   222.00     64.40%
  11138 requests in 30.07s, 4.75MB read
  Socket errors: connect 757, read 279, write 37, timeout 2
Requests/sec:    370.42
Transfer/sec:    161.70KB
```

### Get a post

```
$ wrk -c 500 -t 4 -d 30s http://localhost:8080/v1/posts/5bed38b1170000f186279824
Running 30s test @ http://localhost:8080/v1/posts/5bed38b1170000f186279824
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    70.82ms   62.25ms   1.99s    94.25%
    Req/Sec     0.85k   426.08     2.04k    69.49%
  101360 requests in 30.10s, 43.21MB read
  Socket errors: connect 253, read 320, write 11, timeout 70
Requests/sec:   3367.99
Transfer/sec:      1.44MB

$ wrk -c 500 -t 8 -d 30s http://localhost:8080/v1/posts/5bed38b1170000f186279824
Running 30s test @ http://localhost:8080/v1/posts/5bed38b1170000f186279824
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    65.90ms   32.69ms 361.07ms   72.06%
    Req/Sec   461.79    224.00     1.31k    69.70%
  110429 requests in 30.09s, 47.08MB read
  Socket errors: connect 253, read 116, write 4, timeout 0
Requests/sec:   3669.66
Transfer/sec:      1.56MB

$ wrk -c 1000 -t 4 -d 30s http://localhost:8080/v1/posts/5bed38b1170000f186279824
Running 30s test @ http://localhost:8080/v1/posts/5bed38b1170000f186279824
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    64.96ms   33.83ms 330.98ms   72.89%
    Req/Sec     0.95k   322.85     2.80k    59.78%
  113777 requests in 30.10s, 48.50MB read
  Socket errors: connect 753, read 178, write 6, timeout 0
Requests/sec:   3779.66
Transfer/sec:      1.61MB

$ wrk -c 1000 -t 8 -d 30s http://localhost:8080/v1/posts/5bed505d10000034e3ea8294
Running 30s test @ http://localhost:8080/v1/posts/5bed505d10000034e3ea8294
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    68.56ms   53.77ms   1.98s    90.02%
    Req/Sec   442.68    528.11     1.98k    77.00%
  102479 requests in 30.09s, 44.66MB read
  Socket errors: connect 757, read 320, write 7, timeout 7
Requests/sec:   3405.79
Transfer/sec:      1.48MB
```

## Spring webflux

### Create a post

```
$ wrk -c 500 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    41.44ms   27.27ms 396.72ms   81.14%
    Req/Sec     1.50k     1.06k    4.26k    68.00%
  179382 requests in 30.08s, 26.69MB read
  Socket errors: connect 253, read 0, write 0, timeout 0
Requests/sec:   5962.54
Transfer/sec:      0.89MB

$ wrk -c 500 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    48.32ms   50.73ms   1.95s    93.42%
    Req/Sec   740.89    651.99     2.32k    61.24%
  154238 requests in 30.10s, 22.95MB read
  Socket errors: connect 253, read 5, write 1, timeout 23
Requests/sec:   5124.14
Transfer/sec:    780.64KB

$ wrk -c 1000 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    36.16ms   22.65ms 450.17ms   83.13%
    Req/Sec     1.71k     1.41k    4.22k    63.47%
  203962 requests in 30.07s, 30.34MB read
  Socket errors: connect 753, read 126, write 68, timeout 0
Requests/sec:   6782.80
Transfer/sec:      1.01MB

$ wrk -c 1000 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    36.84ms   23.51ms 373.72ms   82.30%
    Req/Sec     1.65k     1.15k    4.33k    55.17%
  197816 requests in 30.10s, 29.43MB read
  Socket errors: connect 757, read 127, write 18, timeout 0
Requests/sec:   6572.11
Transfer/sec:      0.98MB
```

### Get a post

```
$ wrk -c 500 -t 4 -d 30s http://localhost:8080/v1/posts/5bec10f83a395113af848871
Running 30s test @ http://localhost:8080/v1/posts/5bec10f83a395113af848871
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    36.27ms   20.90ms 396.74ms   80.53%
    Req/Sec     3.44k   607.37     4.81k    66.17%
  205662 requests in 30.09s, 32.56MB read
  Socket errors: connect 253, read 107, write 24, timeout 0
Requests/sec:   6835.16
Transfer/sec:      1.08MB

$ wrk -c 500 -t 8 -d 30s http://localhost:8080/v1/posts/5bec10f83a395113af848871
Running 30s test @ http://localhost:8080/v1/posts/5bec10f83a395113af848871
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    36.49ms   23.19ms 402.01ms   81.68%
    Req/Sec     1.68k   353.14     2.65k    69.42%
  200917 requests in 30.05s, 31.81MB read
  Socket errors: connect 253, read 92, write 57, timeout 0
Requests/sec:   6685.59
Transfer/sec:      1.06MB

$ wrk -c 1000 -t 4 -d 30s http://localhost:8080/v1/posts/5bec10f83a395113af848871
Running 30s test @ http://localhost:8080/v1/posts/5bec10f83a395113af848871
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    35.60ms   20.76ms 411.18ms   81.69%
    Req/Sec     6.96k     1.38k    9.60k    71.67%
  207846 requests in 30.08s, 32.90MB read
  Socket errors: connect 753, read 0, write 0, timeout 0
Requests/sec:   6909.20
Transfer/sec:      1.09MB

$ wrk -c 1000 -t 8 -d 30s http://localhost:8080/v1/posts/5bec10f83a395113af848871
Running 30s test @ http://localhost:8080/v1/posts/5bec10f83a395113af848871
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    37.17ms   24.37ms 558.02ms   84.62%
    Req/Sec     0.94k     1.06k    4.70k    86.09%
  196397 requests in 30.08s, 31.09MB read
  Socket errors: connect 757, read 10, write 0, timeout 0
Requests/sec:   6528.66
Transfer/sec:      1.03MB
```
