# Play vs Spring Webflux vs Django vs Golang
Comparing play framework (in java and scala), spring webflux, python Django, and golang.

This test implements an API REST with 2 endpoints:
 - A POST resource to store a document in mongoDB.
 - A GET resource to query a document from mongoDB.

There is no tuning in any of the platforms except setting up the mongoDB connection pool to 100 connections.

**Summary performance** (with `wrk -c 1000 -t 8`):

| Framework | TPS (create) | TPS (get) | %CPU | Mem (MB) |
| --------- | ------------ | --------- | ---- | -------- |
| Play java | 5100.54 | 5567.71 | 280% | 994MB |
| Play java reactive | 3708.87 | 5438.03 | 260% | 1016MB |
| Play scala | 453.86 | 3353.32 | 280% | 1592MB |
| Spring webflux | 6572.11 | 6528.66 | 270% | 1314MB |
| Python Django | 152.27 | 149.61 | 100% | 62MB |
| Golang | 12762.16 | 11995.86 | 200% | 18MB |

**NOTE**: By default, spring opens 100 connections during the load test, while play scenarios only open 10 connections with the default configuration. After setting up the pool to 100 connections (for a fair comparison), there is a minor difference. It looks like the pool size is not really relevant for the final results.

**NOTE**: Python Django only uses a CPU core (about 100% CPU consumption) while Java/Scala alternatives are multithreaded and reach up to 300% CPU (with average 270%). Golang approach is multithreaded and it gets best performance without exceeding 200% CPU.

# Conclusions

Go is clearly the best approach in terms of:
 - Maximum performance
 - Miminal memory consumption
 - Delivery is only one executable file with minimal size.
 - Very few dependencies are required
 - Minimal startup time (very relevant when developer needs to wait minutes to pass a small amount of unit tests)
 - Safest approach
 - Minimal learning curve
 - Very clear source code (no need of promises/futures/observables)

# Running examples

## Play Java

```
cd play/play-java
sbt stage
target/universal/stage/bin/play -Dhttp.port=8080 -Dplay.http.secret.key='dfaadfasd'
```

## Play Java Reactive

```
cd play/play-java-reactive
sbt stage
target/universal/stage/bin/play -Dhttp.port=8080 -Dplay.http.secret.key='dfaadfasd'
```

## Play Scala

```
cd play/play-scala
sbt stage
target/universal/stage/bin/play-scala -Dhttp.port=8080 -Dplay.http.secret.key='dfaadfasd'
```

## Spring webflux

```
cd spring
mvn install
java -jar target/spring-0.0.1-SNAPSHOT.jar
```

## Python Django

```
pip install django djangorestframework mongoengine django-rest-framework-mongoengine
cd django
python manage.py runserver 8080
```

## Golang

```
cd golang
go build
./golang
```

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

## Play java (non reactive approach)

### Create a post

```
$ wrk -c 500 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    63.02ms   29.98ms 344.36ms   75.69%
    Req/Sec     0.98k   662.17     2.89k    81.54%
  117458 requests in 30.08s, 25.65MB read
  Socket errors: connect 253, read 21, write 0, timeout 0
Requests/sec:   3905.10
Transfer/sec:      0.85MB

$ wrk -c 500 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    47.82ms   18.79ms 284.28ms   75.29%
    Req/Sec   638.84    363.91     1.53k    64.03%
  152297 requests in 30.10s, 33.26MB read
  Socket errors: connect 253, read 37, write 0, timeout 0
Requests/sec:   5059.82
Transfer/sec:      1.11MB

$ wrk -c 1000 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    49.13ms   18.98ms 241.62ms   75.57%
    Req/Sec     1.26k   570.32     2.47k    65.92%
  150626 requests in 30.06s, 32.90MB read
  Socket errors: connect 753, read 62, write 0, timeout 0
Requests/sec:   5010.62
Transfer/sec:      1.09MB

$ wrk -c 1000 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    47.44ms   18.53ms 191.49ms   74.80%
    Req/Sec   644.09    784.98     3.16k    87.45%
  153507 requests in 30.10s, 33.52MB read
  Socket errors: connect 757, read 77, write 16, timeout 0
Requests/sec:   5100.54
Transfer/sec:      1.11MB
```

### Get a post

```
$ wrk -c 500 -t 4 -d 30s http://localhost:8080/v1/posts/5bed883aa4b79e2cf9f3a0dd
Running 30s test @ http://localhost:8080/v1/posts/5bed883aa4b79e2cf9f3a0dd
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    67.09ms   68.06ms   1.60s    98.47%
    Req/Sec     0.99k   256.05     1.70k    67.56%
  117580 requests in 30.06s, 26.24MB read
  Socket errors: connect 253, read 68, write 0, timeout 0
Requests/sec:   3910.91
Transfer/sec:      0.87MB

$ wrk -c 500 -t 8 -d 30s http://localhost:8080/v1/posts/5bed883aa4b79e2cf9f3a0dd
Running 30s test @ http://localhost:8080/v1/posts/5bed883aa4b79e2cf9f3a0dd
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    45.05ms   28.29ms 923.26ms   97.45%
    Req/Sec   702.26    594.34     3.08k    45.52%
  165574 requests in 30.06s, 36.95MB read
  Socket errors: connect 253, read 102, write 1, timeout 0
Requests/sec:   5507.85
Transfer/sec:      1.23MB

$ wrk -c 1000 -t 4 -d 30s http://localhost:8080/v1/posts/5bed883aa4b79e2cf9f3a0dd
Running 30s test @ http://localhost:8080/v1/posts/5bed883aa4b79e2cf9f3a0dd
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    45.63ms   23.51ms 819.48ms   94.36%
    Req/Sec     2.75k     1.67k    5.99k    58.56%
  163233 requests in 30.09s, 36.43MB read
  Socket errors: connect 753, read 54, write 15, timeout 0
Requests/sec:   5424.42
Transfer/sec:      1.21MB

$ wrk -c 1000 -t 8 -d 30s http://localhost:8080/v1/posts/5bed883aa4b79e2cf9f3a0dd
Running 30s test @ http://localhost:8080/v1/posts/5bed883aa4b79e2cf9f3a0dd
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    42.84ms   17.32ms 789.97ms   91.99%
    Req/Sec     1.14k     1.21k    3.76k    66.80%
  167579 requests in 30.10s, 37.40MB read
  Socket errors: connect 757, read 198, write 61, timeout 0
Requests/sec:   5567.71
Transfer/sec:      1.24MB
```

## Play java (reactive approach)

### Create a post

```
$ wrk -c 500 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    69.98ms   76.47ms   2.00s    98.36%
    Req/Sec     0.92k   579.24     2.27k    52.56%
  109746 requests in 30.08s, 46.26MB read
  Socket errors: connect 253, read 293, write 24, timeout 5
Requests/sec:   3647.89
Transfer/sec:      1.54MB

$ wrk -c 500 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    84.82ms   67.43ms   1.99s    95.94%
    Req/Sec   586.48    313.13     1.26k    65.19%
  84580 requests in 30.10s, 35.65MB read
  Socket errors: connect 253, read 279, write 28, timeout 59
Requests/sec:   2809.85
Transfer/sec:      1.18MB

$ wrk -c 1000 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    65.05ms   61.34ms   1.96s    98.29%
    Req/Sec     0.94k   575.97     2.17k    52.72%
  112070 requests in 30.06s, 47.24MB read
  Socket errors: connect 753, read 309, write 74, timeout 2
Requests/sec:   3728.02
Transfer/sec:      1.57MB

$ wrk -c 1000 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    64.90ms   42.75ms   1.30s    93.97%
    Req/Sec   755.74    441.84     2.99k    64.78%
  111666 requests in 30.11s, 47.07MB read
  Socket errors: connect 757, read 220, write 29, timeout 0
Requests/sec:   3708.87
Transfer/sec:      1.56MB
```

### Get a post

```
$ wrk -c 500 -t 4 -d 30s http://localhost:8080/v1/posts/5beef289bcc1513ff8e67903
Running 30s test @ http://localhost:8080/v1/posts/5beef289bcc1513ff8e67903
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    52.15ms   40.77ms 784.61ms   89.59%
    Req/Sec     1.23k     1.16k    3.83k    56.37%
  143044 requests in 30.10s, 61.66MB read
  Socket errors: connect 253, read 291, write 22, timeout 0
Requests/sec:   4752.67
Transfer/sec:      2.05MB

$ wrk -c 500 -t 8 -d 30s http://localhost:8080/v1/posts/5beef289bcc1513ff8e67903
Running 30s test @ http://localhost:8080/v1/posts/5beef289bcc1513ff8e67903
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    52.53ms   56.37ms   1.01s    92.32%
    Req/Sec   847.09    593.08     2.70k    57.03%
  147225 requests in 30.10s, 63.46MB read
  Socket errors: connect 253, read 265, write 2, timeout 0
Requests/sec:   4890.85
Transfer/sec:      2.11MB

$ wrk -c 1000 -t 4 -d 30s http://localhost:8080/v1/posts/5beef289bcc1513ff8e67903
Running 30s test @ http://localhost:8080/v1/posts/5beef289bcc1513ff8e67903
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    46.47ms   52.59ms 920.42ms   94.00%
    Req/Sec     1.30k   813.51     4.30k    67.45%
  154757 requests in 30.07s, 66.71MB read
  Socket errors: connect 753, read 293, write 31, timeout 0
Requests/sec:   5146.82
Transfer/sec:      2.22MB

$ wrk -c 1000 -t 8 -d 30s http://localhost:8080/v1/posts/5beef289bcc1513ff8e67903
Running 30s test @ http://localhost:8080/v1/posts/5beef289bcc1513ff8e67903
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    44.59ms   48.51ms 893.85ms   94.46%
    Req/Sec   786.30    635.63     2.73k    48.87%
  163604 requests in 30.09s, 70.52MB read
  Socket errors: connect 757, read 304, write 27, timeout 0
Requests/sec:   5438.03
Transfer/sec:      2.34MB
```

## Play scala

### Create a post

```
$ wrk -c 500 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   464.10ms  388.20ms   2.00s    78.78%
    Req/Sec   170.09    113.88   484.00     59.95%
  14368 requests in 30.04s, 6.12MB read
  Socket errors: connect 253, read 281, write 11, timeout 138
Requests/sec:    478.37
Transfer/sec:    208.82KB

$ wrk -c 500 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   463.94ms  394.18ms   1.99s    79.66%
    Req/Sec    82.91     51.94   373.00     66.37%
  14159 requests in 30.08s, 6.04MB read
  Socket errors: connect 253, read 252, write 6, timeout 172
Requests/sec:    470.75
Transfer/sec:    205.49KB

$ wrk -c 1000 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   480.50ms  396.64ms   2.00s    77.82%
    Req/Sec   118.34     67.45   440.00     62.07%
  14067 requests in 30.09s, 6.00MB read
  Socket errors: connect 753, read 266, write 67, timeout 125
Requests/sec:    467.51
Transfer/sec:    204.08KB

$ wrk -c 1000 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   510.06ms  400.67ms   2.00s    65.32%
    Req/Sec   117.82     83.28   564.00     61.99%
  13628 requests in 30.03s, 5.81MB read
  Socket errors: connect 757, read 117, write 54, timeout 131
Requests/sec:    453.86
Transfer/sec:    198.12KB
```

### Get a post

```
$ wrk -c 500 -t 4 -d 30s http://localhost:8080/v1/posts/5bed62b20f000079618c2411
Running 30s test @ http://localhost:8080/v1/posts/5bed62b20f000079618c2411
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    65.04ms   32.63ms 344.82ms   70.89%
    Req/Sec     0.91k   527.90     2.09k    52.67%
  108628 requests in 30.10s, 47.34MB read
  Socket errors: connect 253, read 299, write 13, timeout 0
Requests/sec:   3609.44
Transfer/sec:      1.57MB

$ wrk -c 500 -t 8 -d 30s http://localhost:8080/v1/posts/5bed62b20f000079618c2411
Running 30s test @ http://localhost:8080/v1/posts/5bed62b20f000079618c2411
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    76.50ms   48.61ms 474.58ms   75.31%
    Req/Sec   406.80    333.58     1.30k    54.43%
  95096 requests in 30.07s, 41.45MB read
  Socket errors: connect 253, read 49, write 1, timeout 0
Requests/sec:   3162.29
Transfer/sec:      1.38MB

$ wrk -c 1000 -t 4 -d 30s http://localhost:8080/v1/posts/5bed62b20f000079618c2411
Running 30s test @ http://localhost:8080/v1/posts/5bed62b20f000079618c2411
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    69.59ms   43.46ms 497.56ms   73.38%
    Req/Sec     0.87k   809.94     2.63k    66.92%
  101389 requests in 30.07s, 44.19MB read
  Socket errors: connect 753, read 266, write 4, timeout 0
Requests/sec:   3371.22
Transfer/sec:      1.47MB

$ wrk -c 1000 -t 8 -d 30s http://localhost:8080/v1/posts/5bed62b20f000079618c2411
Running 30s test @ http://localhost:8080/v1/posts/5bed62b20f000079618c2411
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    71.89ms   43.83ms 562.46ms   74.98%
    Req/Sec   849.70    618.43     2.79k    54.84%
  100955 requests in 30.11s, 44.00MB read
  Socket errors: connect 757, read 0, write 0, timeout 0
Requests/sec:   3353.32
Transfer/sec:      1.46MB
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

## Python Django

### Create a post

```
$ wrk -c 500 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts/
Running 30s test @ http://localhost:8080/v1/posts/
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    79.42ms   37.51ms 501.98ms   93.02%
    Req/Sec    42.34     21.86   130.00     61.84%
  4942 requests in 30.10s, 1.44MB read
  Socket errors: connect 253, read 5882, write 242, timeout 0
Requests/sec:    164.19
Transfer/sec:     49.07KB

$ wrk -c 500 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts/
Running 30s test @ http://localhost:8080/v1/posts/
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    93.82ms   54.96ms 479.91ms   91.18%
    Req/Sec    33.88     22.75   131.00     62.56%
  4579 requests in 30.10s, 1.34MB read
  Socket errors: connect 253, read 5504, write 258, timeout 0
Requests/sec:    152.13
Transfer/sec:     45.47KB

$ wrk -c 1000 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts/
Running 30s test @ http://localhost:8080/v1/posts/
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   119.68ms   93.55ms 706.14ms   90.12%
    Req/Sec    38.34     24.23   200.00     69.69%
  4342 requests in 30.05s, 1.27MB read
  Socket errors: connect 753, read 5261, write 327, timeout 0
Requests/sec:    144.47
Transfer/sec:     43.17KB

$ wrk -c 1000 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts/
Running 30s test @ http://localhost:8080/v1/posts/
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   102.64ms   66.73ms 585.40ms   88.83%
    Req/Sec    50.07     39.44   430.00     68.92%
  4583 requests in 30.10s, 1.34MB read
  Socket errors: connect 757, read 5445, write 320, timeout 0
Requests/sec:    152.27
Transfer/sec:     45.51KB
```

### Get a post

```
$ wrk -c 500 -t 4 -d 30s http://localhost:8080/v1/posts/5beebd52fa77d52dc486d903/
Running 30s test @ http://localhost:8080/v1/posts/5beebd52fa77d52dc486d903/
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    85.87ms   42.52ms 532.99ms   92.26%
    Req/Sec    41.51     23.77   210.00     70.97%
  4830 requests in 30.10s, 1.50MB read
  Socket errors: connect 253, read 5701, write 242, timeout 0
Requests/sec:    160.46
Transfer/sec:     50.93KB

$ wrk -c 500 -t 8 -d 30s http://localhost:8080/v1/posts/5beebd52fa77d52dc486d903/
Running 30s test @ http://localhost:8080/v1/posts/5beebd52fa77d52dc486d903/
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   144.49ms  129.35ms 990.10ms   89.73%
    Req/Sec    26.68     22.90   242.00     81.20%
  4285 requests in 30.09s, 1.33MB read
  Socket errors: connect 253, read 5391, write 332, timeout 0
Requests/sec:    142.41
Transfer/sec:     45.20KB

$ wrk -c 1000 -t 4 -d 30s http://localhost:8080/v1/posts/5beebd52fa77d52dc486d903/
Running 30s test @ http://localhost:8080/v1/posts/5beebd52fa77d52dc486d903/
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   104.24ms   80.80ms 878.07ms   91.17%
    Req/Sec    40.74     26.22   198.00     71.06%
  4665 requests in 30.03s, 1.45MB read
  Socket errors: connect 753, read 5691, write 307, timeout 0
Requests/sec:    155.34
Transfer/sec:     49.30KB

$ wrk -c 1000 -t 8 -d 30s http://localhost:8080/v1/posts/5beebd52fa77d52dc486d903/
Running 30s test @ http://localhost:8080/v1/posts/5beebd52fa77d52dc486d903/
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   512.83ms  453.49ms   1.93s    75.79%
    Req/Sec    39.76     28.70   300.00     72.93%
  4503 requests in 30.10s, 1.40MB read
  Socket errors: connect 757, read 7529, write 168, timeout 5
Requests/sec:    149.61
Transfer/sec:     47.49KB
```

## Golang

### Create a post

```
$ wrk -c 500 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    21.09ms    6.51ms 125.01ms   80.74%
    Req/Sec     2.93k     2.25k    7.57k    73.50%
  349536 requests in 30.04s, 63.00MB read
  Socket errors: connect 253, read 37, write 0, timeout 0
Requests/sec:  11634.90
Transfer/sec:      2.10MB

$ wrk -c 500 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    23.36ms    4.84ms  78.94ms   88.35%
    Req/Sec     1.74k     1.05k    3.42k    59.26%
  311999 requests in 30.08s, 56.24MB read
  Socket errors: connect 253, read 0, write 0, timeout 0
Requests/sec:  10371.19
Transfer/sec:      1.87MB

$ wrk -c 1000 -t 4 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    21.38ms    5.78ms 120.86ms   86.51%
    Req/Sec     3.85k     2.13k    7.94k    65.89%
  344705 requests in 30.07s, 62.13MB read
  Socket errors: connect 753, read 37, write 0, timeout 0
Requests/sec:  11464.08
Transfer/sec:      2.07MB

$ wrk -c 1000 -t 8 -s post.lua -d 30s http://localhost:8080/v1/posts
Running 30s test @ http://localhost:8080/v1/posts
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    18.96ms    3.36ms  73.13ms   87.70%
    Req/Sec     2.57k     2.25k    6.51k    50.40%
  384062 requests in 30.09s, 69.23MB read
  Socket errors: connect 757, read 41, write 0, timeout 0
Requests/sec:  12762.16
Transfer/sec:      2.30MB
```

### Get a post

```
$ wrk -c 500 -t 4 -d 30s http://localhost:8080/v1/posts/5beef545699a6e40e84a5730
Running 30s test @ http://localhost:8080/v1/posts/5beef545699a6e40e84a5730
  4 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    22.12ms    8.61ms 184.85ms   91.99%
    Req/Sec     2.81k     1.11k    5.43k    69.58%
  335959 requests in 30.08s, 63.76MB read
  Socket errors: connect 253, read 37, write 0, timeout 0
Requests/sec:  11169.41
Transfer/sec:      2.12MB

$ wrk -c 500 -t 8 -d 30s http://localhost:8080/v1/posts/5beef545699a6e40e84a5730
Running 30s test @ http://localhost:8080/v1/posts/5beef545699a6e40e84a5730
  8 threads and 500 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    20.40ms    3.15ms  84.12ms   85.35%
    Req/Sec     1.71k     1.19k    3.57k    50.86%
  357242 requests in 30.09s, 67.80MB read
  Socket errors: connect 253, read 0, write 0, timeout 0
Requests/sec:  11872.05
Transfer/sec:      2.25MB

$ wrk -c 1000 -t 4 -d 30s http://localhost:8080/v1/posts/5beef545699a6e40e84a5730
Running 30s test @ http://localhost:8080/v1/posts/5beef545699a6e40e84a5730
  4 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    20.95ms    4.33ms  73.23ms   86.39%
    Req/Sec     2.95k     2.57k    7.73k    52.42%
  352000 requests in 30.08s, 66.80MB read
  Socket errors: connect 753, read 41, write 0, timeout 0
Requests/sec:  11704.07
Transfer/sec:      2.22MB

$ wrk -c 1000 -t 8 -d 30s http://localhost:8080/v1/posts/5beef545699a6e40e84a5730
Running 30s test @ http://localhost:8080/v1/posts/5beef545699a6e40e84a5730
  8 threads and 1000 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency    20.19ms    2.89ms  70.84ms   85.32%
    Req/Sec     2.01k     1.81k    5.22k    52.22%
  360817 requests in 30.08s, 68.48MB read
  Socket errors: connect 757, read 62, write 55, timeout 0
Requests/sec:  11995.86
Transfer/sec:      2.28MB
```
