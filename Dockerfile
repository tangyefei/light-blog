# syntax=docker/dockerfile:1

# -------------------------
# 构建阶段：使用 Maven + JDK 编译 Spring Boot 应用
# -------------------------
FROM maven:3.9-eclipse-temurin-17 AS builder

# 统一工作目录，后续 COPY/RUN 都基于这里执行。
WORKDIR /workspace

# 先复制 Maven 描述文件和 Maven settings。
# 这样依赖下载可以被 Docker 层缓存，业务代码变化时无需每次重新下载全部依赖。
COPY pom.xml ./
COPY settings.xml settings.xml

# 预下载依赖，提升后续构建速度。
# 如果你使用的是公司私服或特殊 Maven settings，可以在构建时额外挂载 settings.xml。
RUN --mount=type=cache,target=/root/.m2 mvn -B -s settings.xml dependency:go-offline

# 复制源码并打包。
COPY src src

# 跳过测试以加快镜像构建；CI 场景建议在构建镜像前单独运行完整测试。
RUN --mount=type=cache,target=/root/.m2 mvn -B -s settings.xml -DskipTests package

# -------------------------
# 运行阶段：只保留 JRE 和最终 JAR，减小镜像体积
# -------------------------
FROM eclipse-temurin:17-jre AS runner

# 创建非 root 用户运行 Java 进程，降低容器逃逸或误写系统目录的风险。
RUN groupadd --system app && useradd --system --gid app --home-dir /app --shell /usr/sbin/nologin app

WORKDIR /app

# 从构建阶段复制 Spring Boot 可执行 JAR。
# 当前项目 artifactId 为 light-blog-api，版本为 0.0.1-SNAPSHOT。
COPY --from=builder /workspace/target/light-blog-api-0.0.1-SNAPSHOT.jar app.jar

# Spring Boot 默认以 8081 端口对外提供服务，与 application.properties 保持一致。
EXPOSE 8081

# 切换到非 root 用户。
USER app

# 使用 exec 形式启动，确保容器收到 SIGTERM 时 Java 进程能正常退出。
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
