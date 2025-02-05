package pack.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class VNPayConfig {
	public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
	public static String vnp_ReturnUrl = "http://localhost:8080/user/payment-callback";
	public static String vnp_TmnCode = "TLZLVPQQ";
	public static String vnp_HashSecret = "I4IA1W56YJ5Y4597KIV1S0RRMKX52G6S";
}
