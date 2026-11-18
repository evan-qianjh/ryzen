### 接入方让scheduler发送消息

```shell
echo '{"commandCode":"10001","imType":"MEI_TUAN","accountCode":"123","conversationCode":"2120435031268417563","content":"你好呀"}' |
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims.scheduler.send
```

### scheduler让executor发送消息

```shell
echo '{"taskId":10004,"imType":"MEI_TUAN", "executorCode":"001","accountCode":"123","conversationCode":"2120435031268417563","content":"你好呀"}' |
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims.executor.task.request
```

### 接入方让scheduler分配执行器

```shell
echo '{"code":"cmd-001","type":"ASSIGN","imType":"MEI_TUAN","accountCode":"123"}'
```

### 接入方让scheduler释放执行器

```shell
echo '{"code":"cmd-001","type":"RELEASE","imType":"MEI_TUAN","accountCode":"123"}'
```

### scheduler分配executor

```shell
echo '{"commandKey":"cmd-001","executorCode":"001","type":"ASSIGN","assignment":{"code":"assignment-001","imType":"MEI_TUAN","accountCode":"123","fencingToken":1001}}' |
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims.executor.command
```

### scheduler释放executor

```shell
echo '{"commandKey":"cmd-002","executorCode":"001","type":"RELEASE"}' |
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims.executor.command
```

## kafka-public

### topic

| topic                      | 描述                         | 方向                  | executer | scheduler |
|----------------------------|------------------------------|-----------------------|----------|-----------|
| ims.executor.command       | 调度器给执行器下发指令       | scheduler -> executer | R        | W         |
| ims.executor.register      | 执行器向调度器注册           | executer -> scheduler | W        | R         | 
| ims.executor.heartbeat     | 执行器向调度器汇报心跳       | executer -> scheduler | W        | R         | 
| ims.executor.message       | 执行器采集的消息推给调度器   | executer -> scheduler | W        | R         | 
| ims.executor.task.request  | 调度器发给执行器的任务请求   | scheduler -> executer | R        | W         | 
| ims.executor.task.response | 执行器反馈给调度器的任务响应 | executer -> scheduler | W        | R         | 

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

