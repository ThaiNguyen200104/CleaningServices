package pack.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import pack.interceptors.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		// LOGIN INTERCEPTOR
		registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/admin/login", "/user/login", "/user/signup",
				"/staff/login");

		// USER INTERCEPTOR
		registry.addInterceptor(new UserInterceptor()).addPathPatterns("/user/**").excludePathPatterns("/user/login",
				"/user/checkLogin", "/user/logout", "/user/signup", "/user/newUser", "/user/forgotPassword",
				"/user/getOtp", "/user/validateOtp", "/user/verification", "/user/verify", "/user/changePassword",
				"/user/changePassAction");

		// ADMIN INTERCEPTOR
		registry.addInterceptor(new AdminInterceptor()).addPathPatterns("/admin/**").excludePathPatterns("/admin/login",
				"/admin/checkLogin", "/admin/forgotPassword", "/admin/getOtp", "/admin/validateOtp",
				"/admin/verification", "/admin/changePassword", "/admin/changePassAction");

		// STAFF INTERCEPTOR
		registry.addInterceptor(new StaffInterceptor()).addPathPatterns("/staff/**").excludePathPatterns("/staff/login",
				"/staff/checkLogin", "/staff/forgotPassword", "/staff/getOtp", "/staff/validateOtp",
				"/staff/verification", "/staff/changePassword", "/staff/changePassAction");

		WebMvcConfigurer.super.addInterceptors(registry);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		exposeDirectory("upload", registry);
		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
	}

	private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
		Path uploadDir = Paths.get(dirName);
		String uploadPath = uploadDir.toFile().getAbsolutePath();
		if (dirName.startsWith("../")) {
			dirName = dirName.replace("../", "");
		}
		registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + uploadPath + "/");
	}
}
