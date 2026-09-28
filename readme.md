# 工程结构
- ryzen
    - ryzen-framework [框架]
        - ryzen-framework-common [通用]
        - ryzen-framework-gateway [网关]
        - ryzen-framework-http [HTTP]
        - ryzen-framework-security [安全]
        - ryzen-framework-service [服务]
        - ryzen-framework-servlet [Servlet]
        - ryzen-framework-stream [事件流]
        - ryzen-framework-token [令牌]
        - ryzen-framework-websocket [WebSocket]
    - ryzen-gateway [网关]
        - ryzen-gateway-oem [OEM后管网关]
        - ryzen-gateway-open [OpenAPI网关]
        - ryzen-gateway-partner [合作方网关]
        - ryzen-gateway-saas [SaaS后管网关]
        - ryzen-gateway-tenant [租户后管网关]
        - ryzen-gateway-user [用户网关]
    - ryzen-plugin [插件]
        - ryzen-plugin-aliyun [插件-阿里云]
        - ryzen-plugin-aliyun-oss [插件-阿里云OSS]
        - ryzen-plugin-oss [插件-OSS]
        - ryzen-plugin-sms [插件-短信]
    - ryzen-service [服务]
        - ryzen-service-admin [管理服务]
        - ryzen-service-user [用户服务]

# Service Controller
- /cli/**                       面向CLI（开发运维使用）
- /service[/public]/**          面向服务（内部调用）
- /partner/**                   面向合作方 (如支付宝、微信回调、通知)
- /user[/public]/**             面向用户
- /oem[/public]/**              面向OEM
- /tenant[/public]/**           面向租户
- /saas[/public]/**             面向SaaS


# 域名规则
| 参与者 | 接入方式 | 域名 | service前缀 | 备注 |
|---|---|---|---|---|
| 合作端 | API | https://partner-api.qianjh.com/{service}/{partner}/xxx | /partner | |
| 用户端 | APP | https://user-api.qianjh.com/app/{service}[/public]/xxx | /user | |
| 用户端 | Open API | https://user-api.qianjh.com/open/{service}[/public]/xxx | /user | |
| 租户端 | APP | https://tenant-api.qianjh.com/app/{service}[/public]/xxx | /tenant | |
| 租户端 | APP | https://tenant.qianjh.com/api/app/{service}[/public]/xxx | /tenant | 无跨域方式 |
| OEM端 | APP | https://oem-api.qianjh.com/app/{service}[/public]/xxx | /oem | |
| OEM端 | APP | https://oem.qianjh.com/api/app/{service}[/public]/xxx | /oem | 无跨域方式 |
| SaaS端 | APP | https://saas-api.qianjh.com/app/{service}[/public]/xxx | /saas | |
| SaaS端 | APP | https://saas.qianjh.com/api/app/{service}[/public]/xxx | /saas | 无跨域方式 |

# TODO
- stream-outbox
- service share lib


## License

[MIT © Ryzen-2026](./LICENSE)
