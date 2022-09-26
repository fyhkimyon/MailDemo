# MailDemo
基于SpringBoot的邮件发送demo <br>
提供5个邮件发送接口： <br>
+ 发送简单文本邮件
+ 批量发送简单文本邮件
+ 发送Html邮件
+ 发送Html模板邮件
+ 批量发送Html模板邮件
## Usage
部署完毕后访问`host:port/doc.html`调试接口 <br>
配置参数说明
+ host: 邮箱服务器地址
+ username: 发送者邮箱地址
+ password: 发送者授权码
+ nickname: 发送者昵称
+ template-dir: html模板文件目录
### Normal
+ environment
    + java version: 11
    + maven
+ configuration
    + application.yml
    + templates/message.ftl

```bash
mvn package
java -jar MailDemo-1.0-SNAPSHOT.jar
```
### Docker
```bash
git clone https://github.com/fyhkimyon/MailDemo.git
cd MailDemo
docker build -t maildemo:latest .
docker rmi maven:3.8.4-jdk-11
cat > start-maildemo.sh << EOF
#!bash/bash \

docker run \\
    --restart=always \\
    -itd \\
    -p 15005:15005 \\
    --name maildemo \\
    -e MAIL_HOST=smtp.126.com \\
    -e MAIL_USERNAME=xxx@126.com \\
    -e MAIL_PASSWORD=XXXXXX \\
    -e MAIL_NICKNAME=username \\
    -e MAIL_TEMPLATE_DIE=message.ftl \\
    maildemo:latest
EOF
chmod 755 start-maildemo.sh
bash start-maildemo.sh
```