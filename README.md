# JYYXOJSYSTEM
面向教学应用的基于微服务架构的OJ判题系统设计
项目介绍：
本项目是基于Vue 3 + Spring Boot + Spring Cloud微服务 + Docker + DeepSeek的编程题目在线代码评测系统。用户可以提交代码，并通过DeepSeek大模型获取反馈信息。
负责工作：
1.采用 Spring Cloud 和 Nacos 构建微服务架构，将系统拆分为用户管理、题目管理、代码评测等独立服务，实现了服务间的解耦和独立部署。Spring Cloud Gateway 实现 API 网关，统一管理外部请求，负责请求的路由、负载均衡和安全控制。
2.使用Spring Security 实现多级权限管理体系，以及 RBAC策略，细化用户权限，确保不同角色的用户只能访问其权限范围内的资源。 MD5 加盐加密技术对用户密码进行加密存储。
3.运用Docker 容器技术为代码评测模块构建安全的沙箱环境，确保用户代码的执行过程与宿主系统完全隔离。
4.集成 DeepSeek 大模型，通过 API 接口实现代码的智能分析，能够准确识别潜在编程错误，并提供详细的错误信息和修改建议。系统能够根据用户的代码提交记录和评测结果，生成针对性的优化建议和教学指导。
5.使用 RabbitMQ 实现异步任务处理，将代码评测任务放入消息队列，由后台服务异步处理，避免评测任务阻塞主线程。
