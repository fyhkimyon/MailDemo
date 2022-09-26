# MailDemo
基于SpringBoot的邮件发送demo <br>
提供5个邮件发送接口： <br>
+ 发送简单文本邮件
+ 批量发送简单文本邮件
+ 发送Html邮件
+ 发送Html模板邮件
+ 批量发送Html模板邮件
## Usage
部署完毕后访问`host:port/doc.html`调试接口
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
docker build -t maildemo:latest .
```