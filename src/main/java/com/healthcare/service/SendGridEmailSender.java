package com.healthcare.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmailSender {

    @Value("${sendgrid.apiKey}")
    private String sendgridApiKey;

    public void sendEmail(String to, String subject, String body) throws IOException {
        Email from = new Email("mike@gmail.com");  // Replace with your sender email
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendgridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);
            System.out.println("Email sent with status code: " + response.getStatusCode());
        } catch (IOException e) {
            System.err.println("Failed to send email via SendGrid: " + e.getMessage());
            throw e;  // Optionally rethrow or handle accordingly
        }
    }
}
