# JMeter experiments

Install Apache JMeter 5.6.3, set `JMETER_HOME`, complete Labs 2–6, and start the full system. Then run:

```powershell
.\scripts\run-jmeter.ps1 -Users 1 -DurationSeconds 120
.\scripts\run-jmeter.ps1 -Users 10 -DurationSeconds 120
.\scripts\run-jmeter.ps1 -Users 50 -DurationSeconds 120
```

The plan gives each thread a unique user, registers/logs in, creates a seed post, then performs an approximate 70/10/10/10 feed/post/reply/like mix. Results are written to `results.jtl` and the HTML dashboard to `report/`. Remove the old report directory between runs because JMeter refuses to overwrite a populated report.

For the celebrity experiment, create a dedicated author and followers using the API, then run two controlled post bursts: once at 99 followers and once at 100. Compare Kafka consumer lag, Cassandra row growth, post latency, feed latency, and error rate. The important output is your explanation of the write-amplification step change.

