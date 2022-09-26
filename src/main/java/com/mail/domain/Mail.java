package com.mail.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class Mail {

   @ApiModelProperty("收件地址") private String email;

   @ApiModelProperty("收件人昵称") private String nick;

   @ApiModelProperty("邮件主题") private String subject;

   @ApiModelProperty("邮件内容") private String content;

}
