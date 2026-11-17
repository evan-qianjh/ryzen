### 请求：订阅
```json5
{
  "id": "",
  "action":"subscribe",
  "params": [
    "{name}@{param},{param}", // topic
    "{name}@{param}"
  ]
}
```

### 请求：取消订阅
```json5
{
  "id": "",
  "action":"unsubscribe",
  "params": [
    "{name}@{param},{param}", // topic
    "{name}@{param}"
  ]
}
```

### 请求：登录（私有频道需要用到）
```json5
{
  "id": "",
  "action":"login",
  "params": [
    "{token}"
  ]
}
```
### 请求：登出（私有频道需要用到）
```json5
{
  "id": "",
  "action":"logout",
  "params": []
}
```

### 响应：
```json5
{
  "id":"", // 与请求对应，用于给客户端判断消息关联性
   "code": 0, // 0=成功，其他=失败
   "msg": ""  // code描述
}
```

### 推送：
```json5
{
    "topic": "",
    "topicMeta": {
        "name": "", // 主题名
        "params": "", // 主题参数
        "key": "", // 主题唯一键
    },
    "data": {}
}
```