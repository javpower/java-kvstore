# Kvstore: 🚀 纯Java实现的高性能键值存储


`Kvstore` 是一个用纯 Java 编写的高性能键值存储解决方案，它提供了一个轻量级、无需外部依赖的 Redis 替代品。使用 `Kvstore`，您可以轻松地在任何 Java 应用程序中实现快速的数据存储和检索功能，同时支持数据的持久化。

## 🌟 特性

- **无需Redis**：完全用 Java 实现，无需安装或配置 Redis。
- **Spring Boot 集成**：通过 Spring Boot Starter 快速集成进您的 Spring 应用。
- **丰富的API**：提供与 Redis 类似的丰富命令，包括 SET、GET、INCR、DECR、EXPIRE、EXISTS、LPOP、LPUSH、RPOP、RPUSH、TTL
等。
- **高性能**：为缓存和数据存储优化，提供极速的读写性能。
- **易于使用**：简单的 API 设计，让您快速上手。

## 🏎️ 快速开始

### 添加依赖

将 `kvstore-spring-boot-starter` 添加到您的 Spring Boot 项目的 `pom.xml` 文件中：

```xml
<dependency>
  <groupId>io.github.javpower</groupId>
  <artifactId>kvstore-spring-boot-starter</artifactId>
  <version>2.7.13</version>
</dependency>
```

### 使用 KvstoreUtil

通过自动装配的 `KvstoreUtil` 类，您可以在应用中执行各种键值操作：

```java
import com.github.javpower.kvstore.util.KvstoreUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CacheService {

    private final KvstoreUtil kvstoreUtil;

    @Autowired
    public CacheService(KvstoreUtil kvstoreUtil) {
        this.kvstoreUtil = kvstoreUtil;
    }

    public void setKey(String key, String value) {
        kvstoreUtil.set(key, value, null); // ttl 为 null 表示无过期时间
    }

    public String getKey(String key) {
        return kvstoreUtil.get(key);
    }

    // 支持更多操作...
}
```

## 🎯 支持的命令

- `DECR`：减少键的整数值。
- `DEL`：删除一个或多个键。
- `EXISTS`：检查键是否存在。
- `EXPIRE`：为键设置过期时间。
- `GET`：获取键的值。
- `INCR`：增加键的整数值。
- `LPOP`：从列表左侧弹出元素。
- `LPUSH`：在列表左侧推入元素。
- `RPOP`：从列表右侧弹出元素。
- `RPUSH`：在列表右侧推入元素。
- `SET`：设置键的值，可选过期时间。
- `TTL`：获取键剩余的生存时间。

## 🛠️ 原理

`Kvstore` 使用 Java 的集合和并发机制来实现键值存储，提供了一种无需外部服务的缓存解决方案。

## 📝 贡献

我们欢迎任何形式的贡献，包括但不限于报告 bug、提出新特性、提交 pull request 等。更多信息请查看 [Contributing Guidelines](https://github.com/javpower/java-kvstore/blob/master/CONTRIBUTING.md)。

## 📜 许可证

`Kvstore` 是开源软件，遵循 [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可证。

## 💌 鸣谢


---

`Kvstore` 是由 [JavPower](https://github.com/javpower) 开发和维护的。我们期待您的 [反馈](https://github.com/javpower/java-kvstore/issues) 和 [贡献](https://github.com/javpower/java-kvstore/pulls)！

---

<p align="center">
  <a href="https://github.com/javpower/java-kvstore">
    <img src="https://github.com/javpower/java-kvstore/raw/master/docs/logo.png" alt="Kvstore Logo" width="200">
  </a>
  <br><br>
  <a href="https://github.com/sponsors/javpower">
    <img src="https://img.shields.io/badge/sponsor-JavPower-281A3A" alt="Sponsor JavPower">
  </a>
</p>