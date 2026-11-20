### 接入方让scheduler启动账号

```shell
echo '{"code":"cmd-001","type":"STARTUP","imType":"MEI_TUAN","accountCode":"123"}' |
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims.scheduler.command
```

### 接入方让scheduler停止账号

```shell
echo '{"code":"cmd-001","type":"SHUTDOWN","imType":"MEI_TUAN","accountCode":"123"}' |
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims.scheduler.command
```

### 接入方让scheduler发送消息

```shell
echo '{"commandCode":"10001","imType":"MEI_TUAN","accountCode":"123","conversationCode":"2120435031268417563","content":"你好呀"}' |
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims.scheduler.send
```

<hr />

### scheduler分配executor

```shell
echo '{"id":"cmd-001","executorCode":"001","type":"ASSIGN","assignment":{"id":"assignment-001","imType":"MEI_TUAN","accountCode":"123"}}' |
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims.executor.command
```

### scheduler释放executor

```shell
echo '{"id":"cmd-002","executorCode":"001","type":"RELEASE"}' |
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims.executor.command
```

### scheduler让executor发送消息

```shell
echo '{"taskId":10004,"imType":"MEI_TUAN", "executorCode":"001","accountCode":"123","conversationCode":"2120435031268417563","content":"你好呀"}' |
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims.executor.task.request
```

<hr />

## kafka-public

### topic

| topic                      | 描述     | 方向                  | executer | scheduler |
|----------------------------|----------|-----------------------|----------|-----------|
| ims.executor.command       | 指令     | scheduler -> executer | R        | W         |
| ims.executor.register      | 注册     | executer -> scheduler | W        | R         | 
| ims.executor.heartbeat     | 心跳     | executer -> scheduler | W        | R         | 
| ims.executor.status        | 状态     | executer -> scheduler | W        | R         | 
| ims.executor.message       | 消息     | executer -> scheduler | W        | R         |
| ims.executor.task.request  | 任务请求 | scheduler -> executer | R        | W         | 
| ims.executor.task.response | 任务响应 | executer -> scheduler | W        | R         | 

### ACL

- 执行器账号: ryzen-ims-executor-001
- 调度器账号: ryzen-ims-scheduler

## kafka-private

### topic

| topic                       | 描述                       | 方向                | scheduler | 接入方 |
|-----------------------------|----------------------------|---------------------|-----------|--------|
| ims.scheduler.command       | 接入方发给调度器的指令     | 接入方 -> scheduler | R         | W      |
| ims.scheduler.message       | 调度器采集的消息推给接入方 | scheduler -> 接入方 | W         | R      |
| ims.scheduler.send.request  | 接入方发给调度器的发送请求 | 接入方 -> scheduler | R         | W      |
| ims.scheduler.send.response | 接入方发给调度器的发送响应 | scheduler -> 接入方 | W         | R      |

