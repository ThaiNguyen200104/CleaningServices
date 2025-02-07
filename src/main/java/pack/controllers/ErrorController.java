package pack.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {
	@GetMapping("/error")
	public String handleError(HttpServletRequest request, Model model) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		if (status != null) {
			int statusCode = Integer.parseInt(status.toString());

			switch (statusCode) {
			case 400:
				return "error/400";
			case 401:
				return "error/401";
			case 403:
				return "error/403";
			case 404:
				return "error/404";
			case 500:
				return "error/500";
			default:
				return "error/default";
			}
		}
		return "error/default";
	}
}