-- 聊天执行器
CREATE TABLE ims_executor
(
    id                  bigint      NOT NULL PRIMARY KEY,
    im_type             VARCHAR(30) NOT NULL,
    code                VARCHAR(50) NOT NULL,
    account_code        VARCHAR(50) NULL,
    status              VARCHAR(30) NOT NULL,
    current_operation   VARCHAR(30) NOT NULL DEFAULT 'NONE',
    current_task_id     BIGINT NULL,
    registered_time     DATETIME NULL,
    last_heartbeat_time DATETIME NULL,
    error_message       TEXT NULL,
    created_time        DATETIME             DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time        DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    constraint uidx_1
        unique (code, im_type)
) comment '聊天执行器';

CREATE INDEX idx_1
    ON ims_executor (status, last_heartbeat_time);


-- 聊天执行器任务
CREATE TABLE ims_executor_task
(
    id                BIGINT                             NOT NULL PRIMARY KEY,
    command_code      VARCHAR(50)                        NOT NULL,
    im_type           VARCHAR(30)                        NOT NULL,
    account_code      VARCHAR(50)                        NOT NULL,
    conversation_code VARCHAR(50)                        NOT NULL,
    content           TEXT                               NOT NULL,
    status            VARCHAR(30)                        NOT NULL,

    executor_id       BIGINT NULL,

    dispatch_time     DATETIME NULL,
    accepted_time     DATETIME NULL,
    start_time        DATETIME NULL,
    finish_time       DATETIME NULL,
    deadline_time     DATETIME                           NOT NULL,
    error_message     TEXT,
    created_time      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_time      DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    constraint uidx_1
        unique (command_code)
) comment '聊天执行器任务';

CREATE INDEX idx_1
    ON ims_executor_task (account_code, status, created_time);

CREATE INDEX idx_2
    ON ims_executor_task (status, deadline_time);

CREATE INDEX idx_3
    ON ims_executor_task (executor_id, status);
