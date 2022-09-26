package com.mail.service;

import com.mail.domain.JsonResponse;
import com.mail.domain.Mail;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface MailService {

    /**
     * 发送简单文本邮件
     * @param mail
     * @return 是否发送成功
     * @throws UnsupportedEncodingException
     */
    Boolean sendSimpleMail(Mail mail) throws UnsupportedEncodingException;

    /**
     * 发送批量简单文本邮件
     * @param mails
     * @param defaultContext
     * @return
     */
    JsonResponse<List<String>> sendBatchMail(List<Mail> mails, String defaultContext);

    /**
     * 发送Html邮件
     * @param mail
     * @return
     */
    JsonResponse<String> sendHtmlMail(Mail mail);

    /**
     * 发送Html模板邮件
     * @param mail
     * @return
     */
    JsonResponse<String> sendHtmlTemplateMail(Mail mail);

    /**
     * 发送批量Html模板邮件
     * @param mails
     * @param defaultContext
     * @return
     */
    JsonResponse<List<String>> sendBatchHtmlTemplateMail(List<Mail> mails, String defaultContext);
}
