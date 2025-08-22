# SimpleTimer / 简单定时器

[English](#english) | [中文](#中文)

---

## English

### Overview

SimpleTimer is a lightweight, high-performance distributed timer service built with Kotlin and Netty. It provides scheduled task execution with topic-based message publishing and subscription functionality.

### Features

- 🚀 **High Performance**: Built on Netty for excellent network performance
- ⏰ **Delayed Task Scheduling**: Schedule tasks to execute after a specified delay
- 📡 **Topic-based Pub/Sub**: Support for topic-based message publishing and subscription
- 💓 **Heartbeat Mechanism**: Built-in heartbeat for connection health monitoring
- 🔄 **Automatic Cleanup**: Automatic cleanup of timeout channels
- 🛡️ **Thread Safe**: Concurrent-safe operations using ConcurrentHashMap

### Architecture

The project consists of several key components:

- **SimplerTimer**: Main server class that manages Netty server bootstrap
- **TopicManager**: Manages topic subscriptions and message publishing
- **ScheduleTaskProcessor**: Handles delayed task scheduling using HashedWheelTimer
- **Message Protocol**: Custom binary protocol for client-server communication
- **Network Layer**: Message encoding/decoding and channel management

### Message Types

1. **PING/PONG (Type 1)**: Heartbeat messages for connection health
2. **Schedule Task (Type 2)**: Schedule a delayed task
3. **Triggered (Type 3)**: Notification when a scheduled task is triggered

### Quick Start

#### Prerequisites

- Java 21 or higher
- Gradle 7.0 or higher

#### Build and Run

```bash
# Clone the repository
git clone <repository-url>
cd SimpleTimer

# Build the project
./gradlew build

# Run the server (default port: 8080)
./gradlew run

# Or build and run the JAR
./gradlew shadowJar
java -jar build/libs/SimpleTimer-dev.jar
```

#### Configuration

The server starts on port 8080 by default. You can modify the port in `TimerStarter.kt`:

```kotlin
fun main() = SimplerTimer().run(8081) // Custom port
```

### Usage Example

The server accepts binary messages with the following protocol:

```
Message Structure:
- Magic Number (4 bytes): 0x7355608
- Version (4 bytes): 1
- Type (4 bytes): Message type (1=PING/PONG, 2=Schedule, 3=Triggered)
- Topic Length (4 bytes): Length of topic string
- Topic (variable): Topic name
- Delay (8 bytes): Delay in milliseconds
- Payload (variable): Message payload
```

### Dependencies

- **Netty 4.2.4.Final**: High-performance network framework
- **SLF4J 2.0.17**: Logging facade
- **Logback 1.5.18**: Logging implementation
- **Kotlin 2.2.0**: Programming language

### Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

### License

This project is open source. Please check the license file for details.

## Client Library

A cross-platform open source client library is available for SimpleTimer:

- [SimpleTimer-Client (GitHub)](https://github.com/James-Heller/SimpleTimer-Client)

This library makes it easy to connect to the SimpleTimer server, schedule tasks, and subscribe to topics from your own applications.

---

## 中文

### 概述

SimpleTimer 是一个基于 Kotlin 和 Netty 构建的轻量级、高性能分布式定时器服务。它提供延时任务执行功能，并支持基于主题的消息发布订阅。

### 特性

- 🚀 **高性能**: 基于 Netty 构建，提供卓越的网络性能
- ⏰ **延时任务调度**: 支持指定延时后执行的任务调度
- 📡 **基于主题的发布订阅**: 支持基于主题的消息发布和订阅功能
- 💓 **心跳机制**: 内置心跳机制用于连接健康监控
- 🔄 **自动清理**: 自动清理超时的连接通道
- 🛡️ **线程安全**: 使用 ConcurrentHashMap 确保并发安全操作

### 架构

项目由以下核心组件组成：

- **SimplerTimer**: 主服务器类，管理 Netty 服务器启动
- **TopicManager**: 管理主题订阅和消息发布
- **ScheduleTaskProcessor**: 使用 HashedWheelTimer 处理延时任务调度
- **消息协议**: 客户端-服务器通信的自定义二进制协议
- **网络层**: 消息编码/解码和通道管理

### 消息类型

1. **PING/PONG (类型 1)**: 用于连接健康检查的心跳消息
2. **调度任务 (类型 2)**: 调度一个延时任务
3. **任务触发 (类型 3)**: 计划任务被触发时的通知

### 快速开始

#### 前置要求

- Java 21 或更高版本
- Gradle 7.0 或更高版本

#### 构建和运行

```bash
# 克隆仓库
git clone <repository-url>
cd SimpleTimer

# 构建项目
./gradlew build

# 运行服务器（默认端口：8080）
./gradlew run

# 或者构建并运行 JAR 包
./gradlew shadowJar
java -jar build/libs/SimpleTimer-dev.jar
```

#### 配置

服务器默认在 8080 端口启动。你可以在 `TimerStarter.kt` 中修改端口：

```kotlin
fun main() = SimplerTimer().run(8081) // 自定义端口
```

### 使用示例

服务器接受以下协议的二进制消息：

```
消息结构:
- 魔数 (4 字节): 0x7355608
- 版本 (4 字节): 1
- 类型 (4 字节): 消息类型 (1=PING/PONG, 2=调度, 3=触发)
- 主题长度 (4 字节): 主题字符串的长度
- 主题 (可变): 主题名称
- 延时 (8 字节): 延时时间（毫秒）
- 负载 (可变): 消息负载
```

### 依赖项

- **Netty 4.2.4.Final**: 高性能网络框架
- **SLF4J 2.0.17**: 日志门面
- **Logback 1.5.18**: 日志实现
- **Kotlin 2.2.0**: 编程语言

### 贡献

1. Fork 本仓库
2. 创建功能分支
3. 提交你的修改
4. 推送到分支
5. 创建 Pull Request

### 许可证

本项目为开源项目。详细信息请查看许可证文件。

## 客户端库

SimpleTimer 提供了跨平台的开源客户端库，方便你的应用程序接入定时器服务：

- [SimpleTimer-Client (GitHub)](https://github.com/James-Heller/SimpleTimer-Client)

该库支持连接 SimpleTimer 服务器、任务调度和主题订阅等功能。
