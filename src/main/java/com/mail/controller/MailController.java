package com.mail.controller;

import com.mail.domain.JsonResponse;
import com.mail.domain.Mail;
import com.mail.service.MailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@Api(tags = "发送邮件")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class MailController {

    private final MailService mailService;

    @ApiOperation("发送简单文本邮件")
    @PostMapping("/send-simple-mail")
    public JsonResponse<String> sendSimpleMail(@ApiParam("简单文本邮件信息") @RequestBody Mail mail) throws UnsupportedEncodingException {
        if (mailService.sendSimpleMail(mail)) return JsonResponse.success();
        else return JsonResponse.fail();
    }

    @ApiOperation("批量发送简单文本邮件")
    @PostMapping("/send-batch-mail")
    public JsonResponse<List<String>> sendBatchMail(@ApiParam("简单文本邮件信息数组") @RequestBody List<Mail> mails, @ApiParam("默认邮件内容") @RequestParam String defaultContext) throws UnsupportedEncodingException {
        return mailService.sendBatchMail(mails, defaultContext);
    }

    @ApiOperation("发送Html邮件")
    @PostMapping("/send-html-mail")
    public JsonResponse<String> sendHtmlMail(@ApiParam("Html邮件信息") @RequestBody Mail mail) throws UnsupportedEncodingException {
        return mailService.sendHtmlMail(mail);
    }

    @ApiOperation("发送Html模板邮件")
    @PostMapping("/send-html-template-mail")
    public JsonResponse<String> sendHtmlTemplateMail(@ApiParam("Html邮件信息") @RequestBody Mail mail) throws UnsupportedEncodingException {
        return mailService.sendHtmlTemplateMail(mail);
    }

    @ApiOperation("批量发送Html模板邮件")
    @PostMapping("/send-batch-html-template-mail")
    public JsonResponse<List<String>> sendBatchHtmlTemplateMail(@ApiParam("Html邮件信息数组") @RequestBody List<Mail> mails, @ApiParam("默认邮件内容") @RequestParam String defaultContext) throws UnsupportedEncodingException {
        return mailService.sendBatchHtmlTemplateMail(mails, defaultContext);
    }
}