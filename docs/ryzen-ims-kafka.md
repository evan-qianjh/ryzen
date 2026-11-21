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

| topic                         | 描述     | 方向                  | partitions | routeKey     | executer | scheduler |
|-------------------------------|----------|-----------------------|------------|--------------|----------|-----------|
| ims.executor.register         | 注册     | executer -> scheduler | 3          | executorCode | W        | R         | 
| ims.executor.heartbeat        | 心跳     | executer -> scheduler | 3          | executorCode | W        | R         | 
| ims.executor.status           | 状态     | executer -> scheduler | 3          | executorCode | W        | R         | 
| ims.executor.message          | 消息     | executer -> scheduler | 3          | executorCode | W        | R         |
| ims.executor.command.request  | 指令请求 | scheduler -> executer | 1          | executorCode | R        | W         |
| ims.executor.command.response | 指令响应 | executer -> scheduler | 3          | executorCode | W        | R         |
| ims.executor.task.request     | 任务请求 | scheduler -> executer | 1          | executorCode | R        | W         | 
| ims.executor.task.response    | 任务响应 | executer -> scheduler | 3          | executorCode | W        | R         | 

### ACL

- 执行器账号: ryzen-ims-executor-001
- 调度器账号: ryzen-ims-scheduler

## kafka-default (private)

### topic

| topic                          | 描述     | 方向                | partitions | routeKey    | scheduler | 接入方 |
|--------------------------------|----------|---------------------|------------|-------------|-----------|--------|
| ims.scheduler.message          | 消息     | scheduler -> 接入方 | 3          | accountCode | W         | R      |
| ims.scheduler.command.request  | 指令请求 | 接入方 -> scheduler | 3          | accountCode | R         | W      |
| ims.scheduler.command.response | 指令响应 | 接入方 -> scheduler | 3          | accountCode | W         | R      |
| ims.scheduler.task.request     | 任务请求 | 接入方 -> scheduler | 3          | accountCode | R         | W      |
| ims.scheduler.task.response    | 任务响应 | scheduler -> 接入方 | 3          | accountCode | W         | R      |

