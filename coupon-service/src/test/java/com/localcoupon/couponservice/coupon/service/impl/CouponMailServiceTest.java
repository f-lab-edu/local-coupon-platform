package com.localcoupon.couponservice.coupon.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.localcoupon.common.enums.Result;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class CouponMailServiceTest {

  @Mock
  JavaMailSender mailSender;

  @InjectMocks
  CouponMailService couponMailService;

  @Test
  @DisplayName("sendCouponEmail: 정상 전송 시 SUCCESS 반환 및 메일 내용 검증")
  void sendCouponEmail_success() throws Exception {
    // given
    String email = "user@example.com";
    String title = "여름 세일 쿠폰";
    String qrUrl = "https://cdn.example.com/qr/abc.png";

    // 실제 MimeMessage 사용 (내용 검증 가능)
    MimeMessage realMessage = new MimeMessage((Session) null);
    when(mailSender.createMimeMessage()).thenReturn(realMessage);

    // when
    Result result = couponMailService.sendCouponEmail(email, title, qrUrl);

    // then
    assertThat(result).isEqualTo(Result.SUCCESS);
  }

  @Test
  @DisplayName("sendCouponEmail: MessagingException 발생 시 UserCouponException 전환")
  void sendCouponEmail_fail_messagingException() throws Exception {
    // given
    String email = "user@example.com";
    String title = "봄 세일 쿠폰";
    String qrUrl = "https://cdn.example.com/qr/xyz.png";

    // setTo() 내부에서 호출되는 setRecipients가 MessagingException을 던지도록 오버라이드
    MimeMessage badMessage = new MimeMessage((Session) null) {
      @Override
      public void setRecipients(Message.RecipientType type, Address[] addresses)
          throws MessagingException {
        throw new MessagingException("boom");
      }
    };
    when(mailSender.createMimeMessage()).thenReturn(badMessage);

    // when
    UserCouponException ex = assertThrows(UserCouponException.class,
        () -> couponMailService.sendCouponEmail(email, title, qrUrl));

    // then
    assertThat(ex.getErrorCode()).isEqualTo(UserCouponErrorCode.COUPON_MAIL_SEND_FAILED);
  }

  @Test
  @DisplayName("sendCouponEmail: MailException 발생 시 UserCouponException 전환")
  void sendCouponEmail_fail_mailException() throws Exception {
    // given
    String email = "user@example.com";
    String title = "봄 세일 쿠폰";
    String qrUrl = "https://cdn.example.com/qr/xyz.png";

    when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

    doThrow(new MailSendException("boom")).when(mailSender).send(any(MimeMessage.class));

    // when
    UserCouponException ex = assertThrows(UserCouponException.class,
        () -> couponMailService.sendCouponEmail(email, title, qrUrl));

    // then
    assertThat(ex.getErrorCode()).isEqualTo(UserCouponErrorCode.COUPON_MAIL_SEND_FAILED);
  }
}
