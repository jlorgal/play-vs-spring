# play-vs-spring
Comparing play framework and spring boot webflux.

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
```

## Spring boot

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
