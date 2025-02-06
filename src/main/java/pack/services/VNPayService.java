package pack.services;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import pack.config.VNPayConfig;
import pack.models.Payment;
import pack.repositories.PaymentRepository;

@Service
public class VNPayService {
	@Autowired
	private PaymentRepository paymentRepository;

	public String createPaymentUrl(int orderId, double amount) throws UnsupportedEncodingException {

		String vnp_TxnRef = String.valueOf(orderId);
		Map<String, String> vnp_Params = new HashMap<>();
		vnp_Params.put("vnp_Version", "2.1.0");
		vnp_Params.put("vnp_Command", "pay");
		vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
		vnp_Params.put("vnp_Amount", String.valueOf((long) (amount * 100)));
		vnp_Params.put("vnp_CurrCode", "VND");

		vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
		vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
		vnp_Params.put("vnp_OrderType", "other");
		vnp_Params.put("vnp_Locale", "vn");
		vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
		vnp_Params.put("vnp_IpAddr", "127.0.0.1");

		Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		String vnp_CreateDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

		cld.add(Calendar.MINUTE, 15);
		String vnp_ExpireDate = formatter.format(cld.getTime());
		vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

		List fieldNames = new ArrayList(vnp_Params.keySet());
		Collections.sort(fieldNames);
		StringBuilder hashData = new StringBuilder();
		StringBuilder query = new StringBuilder();
		Iterator itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = vnp_Params.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				hashData.append(fieldName);
				hashData.append('=');
				hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
				query.append('=');
				query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				if (itr.hasNext()) {
					query.append('&');
					hashData.append('&');
				}
			}
		}
		String queryUrl = query.toString();
		String vnp_SecureHash = hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
		queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
		return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
	}

	public boolean validatePaymentResponse(HttpServletRequest request) {
		try {
			Map<String, String> fields = new HashMap<>();
			for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
				String fieldName = (String) params.nextElement();
				String fieldValue = request.getParameter(fieldName);
				if ((fieldValue != null) && (fieldValue.length() > 0)) {
					fields.put(fieldName, fieldValue);
				}
			}

			String vnp_SecureHash = request.getParameter("vnp_SecureHash");

			Map<String, String> fieldsCopy = new HashMap<>(fields);
			fieldsCopy.remove("vnp_SecureHashType");
			fieldsCopy.remove("vnp_SecureHash");

			String signValue = hashAllFields(fieldsCopy);
			System.out.println("Calculated SecureHash: " + signValue);

			boolean isValid = signValue.equals(vnp_SecureHash);
			System.out.println("Validation result: " + isValid);

			return isValid;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void processInitialPayment(int orderDetailId, int userId, int amount) {
		paymentRepository.savePayment(orderDetailId, userId, amount);
	}

	public void processVNPayCallback(HttpServletRequest request) {
		try {
			String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
			String vnp_TransactionNo = request.getParameter("vnp_TransactionNo");
			String vnp_TxnRef = request.getParameter("vnp_TxnRef");
			String vnp_BankCode = request.getParameter("vnp_BankCode");
			String vnp_PayDate = request.getParameter("vnp_PayDate");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Date paymentDate;
			try {
				paymentDate = sdf.parse(vnp_PayDate);
			} catch (ParseException e) {
				e.printStackTrace();
				paymentDate = new Date();
			}

			String status = "00".equals(vnp_ResponseCode) ? "successful" : "failed";

			paymentRepository.updatePaymentStatus(vnp_TransactionNo, vnp_BankCode, paymentDate, status,
					vnp_ResponseCode, request.getParameter("vnp_TransactionStatus"), vnp_TxnRef);

			if ("00".equals(vnp_ResponseCode)) {
				paymentRepository.updateOrderStatus(vnp_TxnRef, "completed");

				List<Integer> staffIds = paymentRepository.getStaffIdsByOrderId(Integer.parseInt(vnp_TxnRef));
				paymentRepository.updateStaffStatus(staffIds);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Payment processing failed");
		}
	}

	private String hmacSHA512(String key, String data) {
		try {
			Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
			byte[] hmacKeyBytes = key.getBytes();
			SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
			sha512_HMAC.init(secretKey);
			byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
			byte[] result = sha512_HMAC.doFinal(dataBytes);
			StringBuilder sb = new StringBuilder();
			for (byte b : result) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}

	private String hashAllFields(Map<String, String> fields) {
		List<String> fieldNames = new ArrayList<>(fields.keySet());
		Collections.sort(fieldNames);

		StringBuilder hashData = new StringBuilder();
		Iterator<String> itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = itr.next();
			String fieldValue = fields.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				hashData.append(fieldName);
				hashData.append("=");
				try {
					hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
				} catch (UnsupportedEncodingException e) {
					hashData.append(fieldValue);
				}
				if (itr.hasNext()) {
					hashData.append("&");
				}
			}
		}

		return hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
	}
}
