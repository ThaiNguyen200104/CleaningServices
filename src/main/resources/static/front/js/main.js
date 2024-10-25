(function($) {
	"use strict";

	// Back to top button
	$(window).scroll(function() {
		if ($(this).scrollTop() > 200) {
			$('.back-to-top').fadeIn('slow');
		} else {
			$('.back-to-top').fadeOut('slow');
		}
	});

	$('.back-to-top').click(function() {
		$('html, body').animate({ scrollTop: 0 }, 1500, 'easeInOutExpo');
		return false;
	});

	// Dropdown on mouse hover
	$(document).ready(function() {
		function toggleNavbarMethod() {
			if ($(window).width() > 992) {
				$('.navbar .dropdown').on('mouseover', function() {
					$('.dropdown-toggle', this).trigger('click');
				}).on('mouseout', function() {
					$('.dropdown-toggle', this).trigger('click').blur();
				});
			} else {
				$('.navbar .dropdown').off('mouseover').off('mouseout');
			}
		}
		toggleNavbarMethod();
		$(window).resize(toggleNavbarMethod);
	});

	// Testimonials carousel
	$(".testimonials-carousel").owlCarousel({
		autoplay: true,
		dots: true,
		loop: true,
		responsive: {
			0: {
				items: 1
			},
			576: {
				items: 1
			},
			768: {
				items: 2
			},
			992: {
				items: 3
			}
		}
	});

	// Portfolio isotope and filter
	var portfolioIsotope = $('.portfolio-container').isotope({
		itemSelector: '.portfolio-item',
		layoutMode: 'fitRows'
	});

	$('#portfolio-flters li').on('click', function() {
		$("#portfolio-flters li").removeClass('filter-active');
		$(this).addClass('filter-active');

		portfolioIsotope.isotope({ filter: $(this).data('filter') });
	});
})(jQuery);

setTimeout(function() {
	document.querySelector('.error-message').innerText = '';
}, 6000);

(function() {
	'use strict';
	window.addEventListener('load', function() {
		var forms = document.getElementsByClassName('needs-validation');
		var validation = Array.prototype.filter.call(forms, function(form) {
			form.addEventListener('submit', function(event) {
				if (form.checkValidity() === false) {
					event.preventDefault();
					event.stopPropagation();
				}
				form.classList.add('was-validated');
			}, false);
		});
	}, false);
})();

// Hàm cập nhật đếm ký tự
document.getElementById('message').addEventListener('input', function() {
	const messageLength = this.value.length;
	document.getElementById('charCount').innerText = `${messageLength}/200`;
});

function sendGmail(event) {
	event.preventDefault(); // Ngăn chặn form gửi đi

	const submitBtn = document.getElementById('submitBtn');

	// Vô hiệu hóa nút submit để ngăn chặn gửi liên tục
	submitBtn.disabled = true;

	// Thu thập dữ liệu từ form
	const name = document.getElementById('name').value;
	const email = document.getElementById('email').value;
	const subject = document.getElementById('subject').value;
	const message = document.getElementById('message').value;

	// Kiểm tra độ dài của tin nhắn
	if (message.length > 200) {
		alert('Message quá dài, vui lòng giới hạn dưới 200 ký tự.');
		submitBtn.disabled = false; // Kích hoạt lại nút submit nếu có lỗi
		return;
	}

	// Tạo liên kết mở Gmail với dữ liệu người dùng
	const gmailLink = `https://mail.google.com/mail/?view=cm&fs=1&to=example@example.com&su=${encodeURIComponent(subject)}&body=Name: ${encodeURIComponent(name)}%0AEmail: ${encodeURIComponent(email)}%0A%0AMessage:%0A${encodeURIComponent(message)}`;

	// Chuyển hướng tới Gmail để bắt đầu email mới
	window.open(gmailLink, '_blank');

	// Hiển thị thông báo thành công và reset form
	alert('Tin nhắn đã được gửi thành công!');
	document.querySelector('form').reset();
	document.getElementById('charCount').innerText = '0/200'; // Đặt lại bộ đếm ký tự
	submitBtn.disabled = false; // Kích hoạt lại nút submit cho lần gửi sau
}