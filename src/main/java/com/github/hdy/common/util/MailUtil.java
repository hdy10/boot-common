package com.github.hdy.common.util;

import com.github.hdy.common.spring.SpringContextHolder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 发送邮件工具类
 *
 * @author 贺大爷
 * @date 2019/6/25
 */
public class MailUtil {
    private static JavaMailSender javaMailSender = SpringContextHolder.getApplicationContext().getBean(JavaMailSender.class);

    /**
     * 发送简单邮件
     *
     * @param subject  主题
     * @param text     内容
     * @param receiver 收件人
     *
     * @return
     */
    public static boolean sendSimpleEmail(String subject, String text, String sender, String receiver) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);//发送者.
        message.setTo(receiver);//接收者.
        message.setSubject(subject);//邮件主题.
        message.setText(text);//邮件内容.
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 发送带附件的邮件
     *
     * @param subject  主题
     * @param text     内容
     * @param receiver 收件人
     * @param files    附件集合
     *
     * @return
     *
     * @throws MessagingException
     */
    public static boolean sendAttachmentsEmail(String subject, String text, String sender, String receiver, List<File> files) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //基本设置.
        helper.setFrom(sender);//发送者.
        helper.setTo(receiver);//接收者.
        helper.setSubject(subject);//邮件主题.
        helper.setText(text);//邮件内容.
        //附件1,获取文件对象.
        for (File file : files) {
            FileSystemResource resource = new FileSystemResource(file);
            helper.addAttachment(file.getName(), resource);
        }
        try {
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 发送模板邮件
     *
     * @param subject      主题
     * @param receiver     收件人
     * @param templateName 模板名称
     * @param params       参数
     *
     * @return
     *
     * @throws MessagingException
     * @throws IOException
     * @throws TemplateException
     */
    public static boolean sendTemplateMail(String subject, String sender, String receiver, String templateName, Map<String, Object> params) throws MessagingException, IOException, TemplateException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //基本设置.
        helper.setFrom(sender);//发送者.
        helper.setTo(receiver);//接收者.
        helper.setSubject(subject);//邮件主题.
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        // 设定去哪里读取相应的ftl模板
        cfg.setClassForTemplateLoading(new MailUtil().getClass(), "/templates");
        // 在模板文件目录中寻找名称为name的模板文件
        Template template = cfg.getTemplate(templateName);
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, params);
        helper.setText(html, true);
        try {
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
