(function($) {

	// Preloader
	const preloader = document.querySelectorAll('#preloader');
	window.addEventListener('load', function() {
		if (preloader.length) {
			this.document.getElementById('preloader').style.display = 'none'
		}
	});

	// Back to top button
	$(window).scroll(function() {
		if ($(this).scrollTop() > 200) {
			$('.back-to-top').fadeIn('slow');
		} else {
			$('.back-to-top').fadeOut('slow');
		}
	});

	$('.back-to-top').click(function() {
		$('html, body').animate({ scrollTop: 0 }, 1000, 'easeInOutExpo');
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

	// Popover Button
	$(document).ready(function() {
		$('[data-toggle="popover"]').popover({
			trigger: 'click',
			html: true,
			placement: 'top'
		});
	});
	$(document).on('click', function(e) {
		$('[data-toggle="popover"]').each(function() {
			if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.popover').has(e.target).length === 0) {
				$(this).popover('hide');
			}
		});
	});

})(jQuery);

setTimeout(function() {
	document.querySelector('.error-message').innerText = '';
}, 6000);

// Khóa nút nhấn liên tục
function disableButton() {
	event.preventDefault();

	const button = document.getElementById("sendCodeButton");
	button.disabled = true;

	setTimeout(() => {
		button.disabled = false;
	}, 5000);

	event.target.closest('form').submit();
}

// Booking Service Modal
let selectedServiceId = null;
let UserId = null;

function showBookingPopup(event) {
	var item = event.target
	selectedServiceId = item.getAttribute("serId");
	UserId = item.getAttribute("userId");
	document.getElementById("bookingModal").style.display = "flex";
}

function closePopup() {
	document.getElementById("bookingModal").style.display = "none";
}

function confirmBooking() {
	const date = document.getElementById("serviceDate").value;

	if (!date) {
		alert("Please select a date.");
		return;
	}

	if (UserId == null) {
		window.location.href = '/user/login';
	}

	$.ajax({
		url: '/user/bookService',
		type: 'POST',
		data: {
			userId: UserId,
			serviceId: selectedServiceId,
			startDate: date
		},
		success: function(response, textStatus, xhr) {
			if (xhr.status === 200) {
				showNotification(response, "success");
				closePopup();
			} else {
				showNotification("Failed to create order: " + response, "error");
			}
		},
		error: function(xhr) {
			if (xhr.status === 409) {
				showNotification("Service is already booked.", "warning");
			} else {
				console.error('Error:', xhr.responseText);
				showNotification("Error: " + xhr.responseText, "error");
			}
		}
	});
}

// Notification
function showNotification(message, type) {
	var notification = $('<div class="notification ' + type + '">' + message + '</div>');
	$('body').append(notification);
	notification.fadeIn('slow').delay(3000).fadeOut('slow', function() {
		$(this).remove();
	});
}

const inputDate = document.getElementById("serviceDate");
const currentYear = new Date().getFullYear();
inputDate.max = `${currentYear + 2}-12-31`;