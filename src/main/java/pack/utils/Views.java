package pack.utils;

public class Views {
	// -------------------- ADMIN -------------------- //

	// REGEX
	public static String PHONE_REGEX = "\\d{10,11}";

	// ADMIN - VIEWS
	public static String ADMIN_LOGIN = "admin/login";
	public static String ADMIN_INDEX = "admin/index";
	public static String ADMIN_EDIT_PROFILE = "admin/edit_profile";
	public static String ADMIN_FORGOT_PASSWORD = "admin/forgot_password";
	public static String ADMIN_VALIDATE = "admin/validate_otp";

	// ADMIN/SERVICES
	public static String ADMIN_SERVICES_LIST = "admin/services/list";
	public static String ADMIN_SERVICES_CREATE = "admin/services/create";
	public static String ADMIN_SERVICES_EDIT = "admin/services/edit";

	// ADMIN/STAFFS
	public static String ADMIN_STAFFS_LIST = "admin/staffs/list";
	public static String ADMIN_STAFFS_CREATE_ACCOUNT = "admin/staffs/create_account";

	// ADMIN/BLOGS
	public static String ADMIN_BLOGS_LIST = "admin/blogs/list";
	public static String ADMIN_BLOGS_CREATE = "admin/blogs/create";
	public static String ADMIN_BLOGS_EDIT = "admin/blogs/edit";

	// ADMIN/ORDERS
	public static String ADMIN_ORDERS_LIST = "admin/orders/list";
	public static String ADMIN_ORDERS_REQUEST = "admin/orders/request";
	public static String ADMIN_ORDERS_ASSIGN_STAFF = "admin/orders/assign_staff";
	public static String ADMIN_ORDERS_REPLACE_STAFF = "admin/orders/replaceStaff";
	public static String ADMIN_ORDERS_STAFF_FOR_REPLACE = "admin/orders/staffForReplace";
	public static String ADMIN_REQUESTS_STAFF_FOR_REPLACE = "admin/orders/staffForCancelRequest";

	// ADMMIN/USER_REQUEST
	public static String ADMIN_REQUEST_LIST = "admin/urd/list";
	public static String ADMIN_REQUEST_ASSIGN = "admin/urd/assign";

	// -------------------- STAFF -------------------- //

	// STAFF - VIEWS
	public static String STAFF_LOGIN = "staff/login";
	public static String STAFF_INDEX = "staff/index";
	public static String STAFF_EDIT_PROFILE = "staff/edit_profile";
	public static String STAFF_FORGOT_PASSWORD = "staff/forgot_password";
	public static String STAFF_VALIDATE = "staff/validate_otp";
	public static String STAFF_REQUEST_LIST = "staff/request/list";
	public static String STAFF_ORDER_LIST = "staff/order/list";

	// -------------------- USER -------------------- //

	// USER - VIEWS
	public static String USER_SIGNUP = "user/signup";
	public static String USER_LOGIN = "user/login";
	public static String USER_ACCOUNTS = "user/accounts";
	public static String USER_EDIT_PROFILE = "user/edit_profile";
	public static String USER_FORGOT_PASSWORD = "user/forgot_password";
	public static String USER_VALIDATE = "user/validate_otp";
	public static String USER_CHANGE_PASSWORD = "user/change_password";
	public static String USER_ORDERS = "user/orders";
	public static String USER_ORDER_DETAILS = "user/order_details";
	public static String USER_SEE_MORE = "user/see_more";
	public static String USER_BROWSE_MORE = "user/browse_more";
	public static String USER_CHANGE_SERVICE = "user/change_service";
	public static String USER_PAYMENTS = "user/payment_history";

	// -------------------- HOMEPAGE -------------------- //

	// MAIN PAGE - VIEWS
	public static String MAIN_INDEX = "main/index";
	public static String MAIN_SERVICE = "main/service";
	public static String MAIN_ABOUT = "main/about";
	public static String MAIN_BLOG = "main/blog";
	public static String MAIN_BLOG_DETAIL = "main/blog_detail";
	public static String MAIN_CONTACT = "main/contact";

	// -------------------- DATABASE -------------------- //

	// ADMIN
	public static String TBL_ADMIN = "admin";
	public static String COL_ADMIN_ID = "id";
	public static String COL_ADMIN_USERNAME = "username";
	public static String COL_ADMIN_PASSWORD = "password";
	public static String COL_ADMIN_EMAIL = "email";
	public static String COL_ADMIN_CREATEDATE = "create_date";

	// USERS
	public static String TBL_USER = "users";
	public static String COL_USER_ID = "id";
	public static String COL_USER_FULLNAME = "fullname";
	public static String COL_USER_USERNAME = "username";
	public static String COL_USER_PASSWORD = "password";
	public static String COL_USER_EMAIL = "email";
	public static String COL_USER_PHONE = "phone";
	public static String COL_USER_ADDRESS = "address";
	public static String COL_USER_IMAGES = "image";
	public static String COL_USER_CREATEDATE = "create_date";
	public static String COL_USER_IS_VERIFIED = "is_verified";

	// BLOGS
	public static String TBL_BLOG = "blogs";
	public static String COL_BLOG_ID = "id";
	public static String COL_BLOG_TITLE = "title";
	public static String COL_BLOG_CONTENT = "content";
	public static String COL_BLOG_IMAGES = "image";
	public static String COL_BLOG_CREATEDATE = "create_date";
	public static String COL_BLOG_UPDATEDATE = "update_date";

	// SERVICES
	public static String TBL_SERVICES = "services";
	public static String COL_SERVICES_ID = "id";
	public static String COL_SERVICES_NAME = "service_name";
	public static String COL_SERVICES_DESCRIPTION = "description";
	public static String COL_SERVICES_BASE_PRICE = "base_price";
	public static String COL_SERVICES_STAFF_REQUIRED = "staff_required";
	public static String COL_SERVICES_IMAGES = "image";
	public static String COL_SERVICES_STATUS = "status";

	// ORDERS
	public static String TBL_ORDER = "orders";
	public static String COL_ORDERS_ID = "id";
	public static String COL_ORDERS_USRREQID = "usrReq_id";

	// ORDER_DETAILS
	public static String TBL_ORDER_DETAIL = "order_details";
	public static String COL_ORDER_DETAIL_ID = "id";
	public static String COL_ORDER_DETAIL_ORDER_ID = "order_id";
	public static String COL_ORDER_DETAIL_SERVICE_ID = "service_id";
	public static String COL_ORDER_DETAIL_PRICE = "price";
	public static String COL_ORDER_DETAIL_STARTDATE = "start_date";
	public static String COL_ORDER_DETAIL_COMPLETEDATE = "complete_date";
	public static String COL_ORDER_DETAIL_CREATEDATE = "create_date";
	public static String COL_ORDER_DETAIL_BEFOREIMAGE = "beforeImage";
	public static String COL_ORDER_DETAIL_AFTERIMAGE = "afterImage";
	public static String COL_ORDER_DETAIL_STATUS = "status";

	// USER_REQUESTS
	public static String TBL_USER_REQUEST = "user_requests";
	public static String COL_UR_ID = "id";
	public static String COL_UR_USRID = "user_id";

	// USER_REQUEST_DETAILS
	public static String TBL_USER_REQUEST_DETAILS = "user_request_details";
	public static String COL_URD_ID = "id";
	public static String COL_URD_USRREQID = "usrReq_id";
	public static String COL_URD_USRID = "user_id";
	public static String COL_URD_STAFFID = "staff_id";
	public static String COL_URD_SERID = "service_id";
	public static String COL_URD_PRICE = "price";
	public static String COL_URD_STARTDATE = "start_date";
	public static String COL_URD_CREATEDATE = "create_date";
	public static String COL_URD_STATUS = "status";

	// STAFFS
	public static String TBL_STAFFS = "staffs";
	public static String COL_STAFFS_ID = "id";
	public static String COL_STAFFS_FULLNAME = "fullname";
	public static String COL_STAFFS_USERNAME = "username";
	public static String COL_STAFFS_PASSWORD = "password";
	public static String COL_STAFFS_EMAIL = "email";
	public static String COL_STAFFS_PHONE = "phone";
	public static String COL_STAFFS_IMAGES = "image";
	public static String COL_STAFFS_SALARY = "salary";
	public static String COL_STAFFS_YEAR_EXP = "year_experience";
	public static String COL_STAFFS_CREATEDATE = "create_date";
	public static String COL_STAFFS_STATUS = "status";

	// SCHEDULES
	public static String TBL_SCHEDULES = "schedules";
	public static String COL_SCHEDULES_ID = "id";
	public static String COL_SCHEDULES_STAFF_ID = "staff_id";
	public static String COL_SCHEDULES_DETAIL_ID = "detail_id";
	public static String COL_SCHEDULES_START_DATE = "start_date";
	public static String COL_SCHEDULES_END_DATE = "end_date";
	public static String COL_SCHEDULES_STATUS = "status";

	// SCHEDULE_REQUESTS
	public static String TBL_SCHEDULE_REQUESTS = "schedule_requests";
	public static String COL_SCHEDULE_REQUESTS_ID = "id";
	public static String COL_SCHEDULE_REQUESTS_SCHEDULE_ID = "schedule_id";
	public static String COL_SCHEDULE_REQUESTS_DATE_ADJUST = "date_adjust";
	public static String COL_SCHEDULE_REQUESTS_TYPE = "type";
	public static String COL_SCHEDULE_REQUESTS_REASON = "reason";
	public static String COL_SCHEDULE_REQUESTS_CREATEDATE = "create_date";
	public static String COL_SCHEDULE_REQUESTS_STATUS = "status";

	// PAYMENTS
	public static String TBL_PAYMENTS = "payments";
	public static String COL_PAYMENTS_ID = "id";
	public static String COL_PAYMENTS_ORDER_DETAIL_ID = "order_detail_id";
	public static String COL_PAYMENTS_USR_ID = "user_id";
	public static String COL_PAYMENTS_AMOUNT = "amount";
	public static String COL_PAYMENTS_TRANSACTION_NO = "transaction_no";
	public static String COL_PAYMENTS_BANK_CODE = "bank_code";
	public static String COL_PAYMENTS_STATUS = "status";
	public static String COL_PAYMENTS_TRANSACTION_STATUS = "transaction_status";
	public static String COL_PAYMENTS_RESPONSE_CODE = "response_code";
	public static String COL_PAYMENTS_PAYMENT_DATE = "payment_date";
	public static String COL_PAYMENTS_CREATEDATE = "create_date";

}
