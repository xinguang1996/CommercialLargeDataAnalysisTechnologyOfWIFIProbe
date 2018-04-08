# 基于wifi探针的商业大数据分析技术

## Victors


![](图片/APP图片/logo.png)

## "Victors'WIFI"项目简介

- 本项目为第六届“中国软件杯”大学生软件设计大赛的参赛作品
- 题目：基于WIFI探针的商业大数据分析技术
- 开发团队：Victors
- 指导老师：刘钢
- 队长：汤佳辉
- 队员：郭欣光、赵鹏越

## "Victors'WIFI"之软硬件环境

### 硬件实现

- 主要硬件参数

    1. 50台基于ARM7架构的树莓派集群
    2. SanDisk 32G SD卡若干
    3. 48口千兆交换机一台
    4. Arduino Mega 2560单片机一块
    5. WIFI探针设备若干
    6. SIM900芯片一块
    7. ESP8285 WIFI芯片
    8. UAPT GPS模块
    9. USB-TTL
    10. 开关电源等

### 软件环境

- 搭载系统：Raspbian Jessie （Based on Debian Jessie）
- version-Linux内核：Linux4.9

### 实物拍摄

![](图片/web图片/集群.png)

## "Victors'WIFI"之自制WIFI探针

- 项目全称：基于WIFI探针的商业大数据分析技术---"Victors'WIFI"
- 项目人员：汤佳辉、郭欣光、赵鹏越
- 程序目录：Victors-source/probe
- 开发语言：Python
- 开发工具：Vim7.3

### 自制WIFI探针程序目录相关说明

程序目录中 github-images-folder文件夹与自制WIFI探针程序无关，里面的图片为Github项目README页面的说明。

### 自制WIFI探针主要元件说明

- Raspberry Pi 3B
- SanDisk 32G SD卡
- ESP8285 WIFI芯片
- UART GPS模块
- 陶瓷天线
- USB-TTL
- 杜邦线，亚克力，螺丝螺母等

### 自制WIFI探针功能先关说明

- 采集探测区域内的MAC地址、地理位置、rssi信号强度、与探针大概距离、采集时间等信息
- 与服务端无限通信，定时HTTP POST请求，实现采集数据持久化
- 支持在服务端进行相关配置（服务端IP、端口、路径、发送数据时间间隔及各项阙值）
- 支持个端远程在线控制（sms、微信、手机App以及web客户端）

### 自制WIFI探针采集数据格式说明