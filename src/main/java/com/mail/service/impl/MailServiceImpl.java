package com.mail.service.impl;

import com.google.common.base.Strings;
import com.mail.domain.JsonResponse;
import com.mail.domain.Mail;
import com.mail.service.MailService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;


@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MailServiceImpl implements MailService {

    private final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    private final JavaMailSender mailSender;

    private final FreeMarkerConfigurer freeMarkerConfigurer;

    /**
     * 线程池默认大小为系统核心线程数 * 2
     */
    private final int POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 发送者昵称
     */
    @Value("${spring.mail.nickname}")
    private String nickname;

    /**
     * 发送者邮箱地址
     */
    @Value("${spring.mail.username}")
    private String username;

    /**
     * html模板文件目录
     */
    @Value("${spring.mail.template-dir}")
    private String templateDir;

    /**
     * 获取打印日志函数，用以表示发送方 -> 接收方
     * @param mail 接收方邮件信息
     * @param success 状态信息 0：发送成功 1：发送失败 2：正在发送中
     * @return 日志信息
     */
    private String getInfo(Mail mail, int success) {
        String info = "[" + nickname + "<" + username + ">] -> " + "[" + mail.getNick() + "<" + mail.getEmail() + ">]";
        if (success == 0) info += " sent success...";
        else if (success == 1) info += " sent fail...";
        else info += " is senting...";
        return info;
    }

    /**
     * 发送简单文本邮件
     * @param mail
     * @return 发送是否成功
     * @throws UnsupportedEncodingException
     */
    @Override
    public Boolean sendSimpleMail(Mail mail) throws UnsupportedEncodingException {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(mail.getEmail());
            message.setSubject(mail.getSubject());
            message.setText(mail.getContent());
            String fromByte = new String((nickname + '<' + username + '>')
                    .getBytes("UTF-8"));
            message.setFrom(fromByte);
            mailSender.send(message);
            logger.info(this.getInfo(mail, 0));
            return true;
        } catch (Exception e) {
            logger.info(this.getInfo(mail, 1));
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发送批量简单文本邮件 线程池异步发送
     * @param mails
     * @param defaultContext 默认内容
     * @return
     */
    @Override
    public JsonResponse<List<String>> sendBatchMail(List<Mail> mails, String defaultContext) {
        ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
        Set<Callable<Boolean>> allThreads = new HashSet<>();
        boolean success = true;
        List<String> result = new ArrayList<>();
        try {
            for (Mail mail: mails) {
                if (Strings.isNullOrEmpty(mail.getContent())) {
                    mail.setContent(defaultContext);
                }
                logger.info(this.getInfo(mail, 2));
                allThreads.add(()->{
                    return sendSimpleMail(mail);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
        List<Future<Boolean>> results = null;
        try {
            results = executor.invokeAll(allThreads);
            for (Future<Boolean> future : results) {
                boolean resp = future.get();
                if (!resp) {
                    success = false;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            success = false;
            e.printStackTrace();
        }
        String msg = success ? "所有邮件发送成功" : "部分邮件发送失败";
        return new JsonResponse<>("0", msg, null);
    }

    /**
     * 发送Html邮件
     * @param mail
     * @return
     */
    @Override
    public JsonResponse<String> sendHtmlMail(Mail mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String fromByte = new String((nickname + '<' + username + '>')
                    .getBytes("UTF-8"));
            helper.setFrom(fromByte);
            helper.setTo(mail.getEmail());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getContent(), true);
            mailSender.send(message);
            logger.info(this.getInfo(mail, 0));
            return JsonResponse.success();
        } catch (MessagingException e) {
            logger.info(this.getInfo(mail, 1));
            e.printStackTrace();
            return JsonResponse.fail("500", "消息发送错误");
        } catch (UnsupportedEncodingException e) {
            logger.info(this.getInfo(mail, 1));
            e.printStackTrace();
            return JsonResponse.fail("500", "编码错误");
        }
    }

    /**
     * 发送html模板邮件
     * @param mail
     * @return
     */
    @Override
    public JsonResponse<String> sendHtmlTemplateMail(Mail mail) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Template template = null;
        try {
            template = freeMarkerConfigurer.getConfiguration().getTemplate(templateDir);
        } catch (IOException e) {
            e.printStackTrace();
            return JsonResponse.fail("500", "读取文件失败");
        }
        /**
         * model中配置模板文件${}中的参数内容
         */
        Map<String, Object> model = new HashMap<>();
        model.put("nickname", nickname);
        model.put("nick", mail.getNick());
        model.put("content", mail.getContent());
        model.put("sendTime", simpleDateFormat.format(new Date()));
        String templateHtml = null;
        try {
            templateHtml = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
           e.printStackTrace();
           return JsonResponse.fail("500", "转化模板失败");
        }
        mail.setContent(templateHtml);
        return this.sendHtmlMail(mail);
    }

    /**
     * 发送批量html模板邮件 线程池异步发送
     * @param mails
     * @param defaultContext 默认邮件内容
     * @return
     */
    @Override
    public JsonResponse<List<String>> sendBatchHtmlTemplateMail(List<Mail> mails, String defaultContext) {
        ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
        Set<Callable<JsonResponse<String>>> allThreads = new HashSet<>();
        boolean success = true;
        List<String> result = new ArrayList<>();
        try {
            for (Mail mail: mails) {
                if (Strings.isNullOrEmpty(mail.getContent())) {
                    mail.setContent(defaultContext);
                }
                logger.info(this.getInfo(mail, 2));
                allThreads.add(()->{
                    return sendHtmlTemplateMail(mail);
                });
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }
        try {
            List<Future<JsonResponse<String>>> results = executor.invokeAll(allThreads);
            for (Future<JsonResponse<String>> future : results) {
                JsonResponse<String> resp = future.get();
                if (!"0".equals(resp.getCode())) {
                    success = false;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            success = false;
            e.printStackTrace();
        }
        String msg = success ? "所有邮件发送成功" : "部分邮件发送失败";
        return new JsonResponse<>("0", msg, null);
    }

}