package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponMailService {

    private final JavaMailSender mailSender;

    public Result sendCouponEmail(String email, String couponTitle, String qrImageUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setTo(email);
            helper.setSubject("[쿠폰 알림] " + couponTitle + " 발급이 완료 되었습니다.");
            helper.setText(
                    "<h2>쿠폰이 발급되었습니다!</h2>" +
                            "<p>쿠폰명: <strong>" + couponTitle + "</strong></p>" +
                            "<p>아래 QR코드를 사용해 쿠폰을 이용하세요.</p>" +
                            "<img src='" + qrImageUrl + "'/>",
                    true
            );
            mailSender.send(message);
            return Result.SUCCESS;
        } catch (MessagingException e) {
            log.error("[CouponMailService] 쿠폰 메일 서비스 발송 에러" , e);
            throw new UserCouponException(UserCouponErrorCode.COUPON_MAIL_SEND_FAILED);
        }
    }
}
