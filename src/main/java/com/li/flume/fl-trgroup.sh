# flume-seconda-tr-log-agent.conf
nohup ../bin/flume-ng agent  --name flume-log-agent2a  /usr/local/apache-flume-1.6.0-bin/conf  --conf-file /usr/local/apache-flume-1.6.0-bin/conf/flume-trgroup-log-agent.conf  -Dflume.root.logger=INFO,console >> trout2a.tx 2>&1 &

# flume-seconda-tr-log-agent.conf
nohup ../bin/flume-ng agent  --name flume-log-agent2b  /usr/local/apache-flume-1.6.0-bin/conf  --conf-file /usr/local/apache-flume-1.6.0-bin/conf/flume-trgroup-log-agent.conf  -Dflume.root.logger=INFO,console >> trout2b.tx 2>&1 &

sleep 5

nohup ../bin/flume-ng agent  --name flume-log-agent1  /usr/local/apache-flume-1.6.0-bin/conf  --conf-file /usr/local/apache-flume-1.6.0-bin/conf/flume-trgroup-log-agent.conf  -Dflume.root.logger=INFO,console >> trout1.tx 2>&1 &

