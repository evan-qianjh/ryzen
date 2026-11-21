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
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims-executor-command
```

### scheduler释放executor

```shell
echo '{"id":"cmd-002","executorCode":"001","type":"RELEASE"}' |
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims-executor-command
```

### scheduler让executor发送消息

```shell
echo '{"taskId":10004,"imType":"MEI_TUAN", "executorCode":"001","accountCode":"123","conversationCode":"2120435031268417563","content":"你好呀"}' |
bin/kafka-console-producer.sh --bootstrap-server 192.168.188.3:9092  --topic ims-executor-task.request
```

<hr />

# kafka 

## kafka-public

### topic

topic的设计要考虑ACL的通配

| topic                                      | 描述     | 方向                  | partitions | routeKey     | executer | scheduler |
|--------------------------------------------|----------|-----------------------|------------|--------------|----------|-----------|
| ims.scheduler-to-executor-command-request  | 指令请求 | scheduler -> executer | 1          | executorCode | R        | W         |
| ims.scheduler-to-executor-task-request     | 任务请求 | scheduler -> executer | 1          | executorCode | R        | W         | 
| ims.executor-to-scheduler-register         | 注册     | executer -> scheduler | 3          | executorCode | W        | R         | 
| ims.executor-to-scheduler-heartbeat        | 心跳     | executer -> scheduler | 3          | executorCode | W        | R         | 
| ims.executor-to-scheduler-status           | 状态     | executer -> scheduler | 3          | executorCode | W        | R         | 
| ims.executor-to-scheduler-message          | 消息     | executer -> scheduler | 3          | executorCode | W        | R         |
| ims.executor-to-scheduler-command-response | 指令响应 | executer -> scheduler | 3          | executorCode | W        | R         |
| ims.executor-to-scheduler-task-response    | 任务响应 | executer -> scheduler | 3          | executorCode | W        | R         | 

### ACL

- 执行器账号: ryzen-ims-executor-001
- 调度器账号: ryzen-ims-scheduler

## kafka-default (private)

### topic

| topic                                 | 描述     | 方向                | partitions | routeKey    | scheduler | 接入方 |
|---------------------------------------|----------|---------------------|------------|-------------|-----------|--------|
| ims.app-to-scheduler-command-request  | 指令请求 | 接入方 -> scheduler | 3          | accountCode | R         | W      |
| ims.app-to-scheduler-task-request     | 任务请求 | 接入方 -> scheduler | 3          | accountCode | R         | W      |
| ims.scheduler-to-app-message          | 消息     | scheduler -> 接入方 | 3          | accountCode | W         | R      |
| ims.scheduler-to-app-report           | 汇报     | scheduler -> 接入方 | 3          | accountCode | W         | R      |
| ims.scheduler-to-app-command-response | 指令响应 | 接入方 -> scheduler | 3          | accountCode | W         | R      |
| ims.scheduler-to-app-task-response    | 任务响应 | scheduler -> 接入方 | 3          | accountCode | W         | R      |

