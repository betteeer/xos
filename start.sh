#!/bin/bash

set -e

# 进入项目根目录
cd /var/jenkins_home/workspace/nt-oms

# 停止并删除已经存在的 oms 容器
docker stop oms || true
docker rm oms || true

# 重命名已有的镜像
if docker images -q oms >/dev/null 2>&1; then
  docker images -q oms | awk '{print "docker tag "$1" oms:latest"}' | sh
fi

# 构建 Docker 镜像
docker build -t oms .

# 启动 oms 容器
docker run -d --name oms -p 9011:9011 oms