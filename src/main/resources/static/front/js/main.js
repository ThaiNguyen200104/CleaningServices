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

