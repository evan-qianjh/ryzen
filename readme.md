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

# controller接口路由规则
- /cli/**                       面向CLI（开发运维使用）
- /service[/public]/**          面向服务（内部调用）
- /oem[/public]/**              面向OEM
- /tenant[/public]/**           面向租户
- /saas[/public]/**             面向SaaS
- /user[/public]/**             面向用户
- /open[/public]/**             面向开放接口
- /partner[/public]/**          面向合作方(如支付宝、微信回调、通知)

# TODO

- stream-outbox
- service share lib

## License

[MIT © Ryzen-2026](./LICENSE)
