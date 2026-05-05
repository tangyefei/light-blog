# GitHub Actions CI/CD 配置说明

## 目标

push 到 `main` 分支后自动完成：

```text
# 拉取 GitHub 仓库源码
checkout 源码

# 在 GitHub Actions 机器上构建 Docker 镜像
构建 Docker 镜像

# 把镜像推送到 AWS ECR
推送镜像到 AWS ECR

# SSH 登录 Lightsail 服务器
SSH 登录 Lightsail 服务器

# 服务器从 AWS ECR 拉取新镜像并重启对应服务
拉取新镜像并重启对应服务
```

## 一、AWS ECR 准备

因为项目部署在 AWS Lightsail 上，镜像仓库优先使用 AWS ECR。这样镜像仓库和服务器都在 AWS 体系内，网络路径、权限管理和后续维护都更顺。

1. 进入 AWS Console。
2. 打开 `Elastic Container Registry`。
3. 创建两个私有仓库：

```text
# 后端镜像仓库
light-blog/light-blog-api

# 前端镜像仓库
light-blog/light-blog-fe
```

4. 记录 AWS 区域，例如：

```text
# 首尔区域示例，实际以你的 Lightsail/ECR 区域为准
ap-northeast-2
```

最终镜像地址类似：

```text
# 后端 latest 镜像，123456789012 替换为你的 AWS Account ID
123456789012.dkr.ecr.ap-northeast-2.amazonaws.com/light-blog/light-blog-api:latest

# 前端 latest 镜像，123456789012 替换为你的 AWS Account ID
123456789012.dkr.ecr.ap-northeast-2.amazonaws.com/light-blog/light-blog-fe:latest
```

## 二、AWS IAM 凭证准备

GitHub Actions 需要有权限向 ECR 推送镜像，Lightsail 服务器也需要有权限从 ECR 拉取镜像。

可以先创建一个用于部署的 IAM User，并授予 ECR 推拉镜像权限。学习阶段可以使用 AWS 托管策略：

```text
# 允许推送、拉取和管理 ECR 镜像，学习阶段使用较省事
AmazonEC2ContainerRegistryPowerUser
```

更严格的生产做法是只允许访问这两个仓库，但当前项目先用上面的托管策略更容易跑通。

创建 IAM User 后，生成 Access Key，记录：

```text
# GitHub Actions 和服务器登录 ECR 都会用到
AWS_ACCESS_KEY_ID

# 只在 GitHub Secrets 中保存，不要提交到代码仓库
AWS_SECRET_ACCESS_KEY
```

## 三、服务器 docker-compose.yml 改造

CI/CD 部署时，服务器不再现场 build，而是从 AWS ECR 拉取镜像。服务器上的 `docker-compose.yml` 应把 `api` 和 `frontend` 改成 `image` 形式：

```yaml
api:
  # CI/CD 模式下服务器直接拉取 ECR 镜像，不再在服务器本地 build
  image: ${ECR_REGISTRY}/light-blog/light-blog-api:latest
  container_name: light-blog-api
  restart: unless-stopped
  environment:
    # 使用 docker 环境配置
    SPRING_PROFILES_ACTIVE: docker
    DB_USERNAME: ${DB_USERNAME:-light_blog}
    DB_PASSWORD: ${DB_PASSWORD:-light_blog_password}
    JWT_SECRET: ${JWT_SECRET}
    MYSQL_HOST: mysql
    MYSQL_PORT: 3306
    MYSQL_DATABASE: ${MYSQL_DATABASE:-techblog}
    REDIS_HOST: redis
    REDIS_PORT: 6379
    TZ: Asia/Shanghai
  ports:
    # 只给 Nginx 反向代理使用，Lightsail 防火墙不需要开放 8081
    - "${API_PORT:-8081}:8081"
  depends_on:
    mysql:
      condition: service_healthy
    redis:
      condition: service_healthy

frontend:
  # 前端同样改为从 ECR 拉取镜像
  image: ${ECR_REGISTRY}/light-blog/light-blog-fe:latest
  container_name: light-blog-fe
  restart: unless-stopped
  environment:
    # 生产环境为空字符串时，前端会请求同域名下的 /api
    NEXT_PUBLIC_API_BASE_URL: ${NEXT_PUBLIC_API_BASE_URL:-}
    NODE_ENV: production
    PORT: 3000
  ports:
    # 只给 Nginx 反向代理使用，Lightsail 防火墙不需要开放 3000
    - "${FRONTEND_PORT:-3000}:3000"
  depends_on:
    - api
```

服务器 `.env` 中增加：

```env
# ECR Registry 地址，123456789012 和 region 按你的 AWS 账号替换
ECR_REGISTRY=123456789012.dkr.ecr.ap-northeast-2.amazonaws.com

# AWS 区域，建议和 ECR 所在区域一致
AWS_REGION=ap-northeast-2

# MySQL root 密码
MYSQL_ROOT_PASSWORD=替换为生产强密码

# 业务数据库名
MYSQL_DATABASE=techblog

# 业务数据库用户
DB_USERNAME=light_blog

# 业务数据库密码
DB_PASSWORD=替换为生产强密码

# JWT 签名密钥，建议至少 32 位随机字符串
JWT_SECRET=替换为至少32位随机字符串

# 留空表示前端调用当前域名下的 /api
NEXT_PUBLIC_API_BASE_URL=
```

## 四、服务器首次登录 ECR

ECR 登录密码是短期 token，不能像普通镜像仓库密码一样长期保存。服务器需要安装 AWS CLI，然后通过 AWS CLI 获取临时登录 token。

在服务器上检查 AWS CLI：

```bash
# 查看是否已经安装 AWS CLI
aws --version
```

如果没有安装，在 Ubuntu 上可以执行：

```bash
# 更新 apt 软件包索引
sudo apt update
```

```bash
# 安装 AWS CLI
sudo apt install -y awscli
```

首次配置 AWS 凭证：

```bash
# 配置 AWS Access Key、Secret Key、默认区域和输出格式
aws configure
```

按提示填写：

```text
# 填写 IAM User 的 Access Key
AWS Access Key ID

# 填写 IAM User 的 Secret Key
AWS Secret Access Key

# 填写 ECR 所在区域，例如 ap-northeast-2
Default region name

# 可以填写 json
Default output format
```

服务器手动登录 ECR：

```bash
# 使用 AWS CLI 获取 ECR 临时登录密码，并交给 docker login
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 123456789012.dkr.ecr.ap-northeast-2.amazonaws.com
```

## 五、GitHub Secrets 配置

两个仓库都需要配置同一组 Secrets：

```text
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_REGION
SERVER_HOST
SERVER_USER
SERVER_PORT
SERVER_SSH_KEY
DEPLOY_DIR
```

含义：

```text
AWS_ACCESS_KEY_ID       IAM User 的 Access Key ID
AWS_SECRET_ACCESS_KEY   IAM User 的 Secret Access Key
AWS_REGION              ECR 所在区域，例如 ap-northeast-2
SERVER_HOST             Lightsail Static IP 或域名
SERVER_USER             SSH 用户名，例如 ubuntu
SERVER_PORT             SSH 端口，通常是 22
SERVER_SSH_KEY          SSH 私钥内容
DEPLOY_DIR              服务器部署目录，例如 /home/ubuntu/light-blog
```

GitHub 配置路径：

```text
# GitHub 仓库中的配置入口
Repository -> Settings -> Secrets and variables -> Actions -> New repository secret
```

`SERVER_SSH_KEY` 获取方式是在本机或服务器上使用已有私钥内容，例如：

```bash
# 查看本机默认 SSH 私钥内容，复制完整输出到 SERVER_SSH_KEY
cat ~/.ssh/id_rsa
```

注意复制完整内容，包含：

```text
-----BEGIN OPENSSH PRIVATE KEY-----
...
-----END OPENSSH PRIVATE KEY-----
```

## 六、工作流文件位置

后端仓库：

```text
# 后端 GitHub Actions 工作流文件
light-blog-api/.github/workflows/deploy.yml
```

前端仓库：

```text
# 前端 GitHub Actions 工作流文件
light-blog-fe/.github/workflows/deploy.yml
```

触发条件都是：

```yaml
on:
  push:
    # 只有 push 到 main 分支才触发自动部署
    branches:
      - main
```

## 七、部署流程

后端代码更新：

```bash
# 暂存本次后端修改
git add .

# 提交后端修改
git commit -m "更新后端"

# 推送到 main 后会触发后端 GitHub Actions
git push origin main
```

GitHub Actions 会自动：

```text
# 构建后端镜像
构建 light-blog/light-blog-api 镜像

# 推送 latest 和 commit sha 两个 tag 到 AWS ECR
推送 latest 和 commit sha tag 到 AWS ECR

# SSH 登录服务器
SSH 到服务器

# 服务器登录 ECR
aws ecr get-login-password ... | docker login ...

# 拉取后端最新镜像
docker compose pull api

# 使用新镜像重建后端容器，不在服务器本地 build
docker compose up -d --no-build api
```

前端代码更新：

```bash
# 暂存本次前端修改
git add .

# 提交前端修改
git commit -m "更新前端"

# 推送到 main 后会触发前端 GitHub Actions
git push origin main
```

GitHub Actions 会自动：

```text
# 构建前端镜像
构建 light-blog/light-blog-fe 镜像

# 推送 latest 和 commit sha 两个 tag 到 AWS ECR
推送 latest 和 commit sha tag 到 AWS ECR

# SSH 登录服务器
SSH 到服务器

# 服务器登录 ECR
aws ecr get-login-password ... | docker login ...

# 拉取前端最新镜像
docker compose pull frontend

# 使用新镜像重建前端容器，不在服务器本地 build
docker compose up -d --no-build frontend
```

## 八、验证

查看 GitHub Actions：

```text
# 查看每次自动部署的执行日志
GitHub 仓库 -> Actions
```

服务器查看容器：

```bash
# 进入服务器部署目录
cd /home/ubuntu/light-blog

# 查看容器运行状态
docker compose ps
```

检查网站：

```bash
# 检查首页 HTTPS 是否返回正常响应头
curl -I https://groundedglow.cc
```

检查登录接口：

```bash
# 检查登录接口是否能通过 Nginx /api 转发到后端
curl -i -X POST 'https://groundedglow.cc/api/users/login' -H 'Content-Type: application/json' -d '{"account":"you@example.com","password":"your_password"}'
```

检查服务器是否能拉取 ECR 镜像：

```bash
# 手动拉取后端镜像，确认服务器 ECR 登录和网络都正常
docker compose pull api
```

## 九、学习点

CI/CD 的核心是把手动部署流程自动化：

```text
# 从提交代码到完成服务重启的自动化链路
代码提交 -> 自动构建 -> 镜像推送 -> 远程部署 -> 服务重启
```

Secrets 的核心作用是把敏感信息从代码中移出：

```text
# AWS 凭证
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY

# SSH 登录服务器用的私钥
SERVER_SSH_KEY

# 服务器地址和部署目录
SERVER_HOST
DEPLOY_DIR
```

不要把这些值提交到 Git 仓库。
