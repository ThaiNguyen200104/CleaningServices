let selectedServiceId = null;
let UserId = null;
let price = null;

// Initialize date restrictions on page load
document.addEventListener('DOMContentLoaded', function() {
	const inputDate = document.getElementById("serviceDate");
	const currentYear = new Date().getFullYear();
	// Set maximum date to 2 years from now
	inputDate.max = `${currentYear + 2}-12-31`;
	// Set minimum date to today
	const today = new Date().toISOString().split('T')[0];
	inputDate.min = today;
});

// Show booking modal
function showBookingPopup(event) {
	const item = event.currentTarget;
	selectedServiceId = item.getAttribute("serId");
	UserId = item.getAttribute("userId");
	price = item.getAttribute("serPrice");

	$('#bookingModal').modal('show');
	document.body.classList.add("no-scroll");
}

// Close booking modal
function closePopup() {
	$('#bookingModal').modal('hide');
	document.body.classList.remove("no-scroll");
}

// Handle booking confirmation
function confirmBooking() {
	const date = document.getElementById("serviceDate").value;

	if (!date) {
		showNotification("Please select a date.", "warning");
		return;
	}

	if (UserId == null) {
		window.location.href = '/user/login';
		return;
	}

	$.ajax({
		url: '/user/bookService',
		type: 'POST',
		data: {
			userId: UserId,
			serviceId: selectedServiceId,
			startDate: date,
			price: price
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

// Handle modal events
$('#bookingModal').on('hidden.bs.modal', function() {
	document.body.classList.remove("no-scroll");
});

// Real-time Search
$(document).ready(function() {
	let searchTimeout;
	const searchInput = $('input[name="search"]');
	const serviceGrid = $('.service-grid');

	searchInput.on('input', function() {
		clearTimeout(searchTimeout);

		// Add loading state
		serviceGrid.addClass('loading');

		searchTimeout = setTimeout(() => {
			const query = $(this).val().trim();
			performSearch(query);
		}, 300);
	});

	function performSearch(query) {
		$.ajax({
			url: '/service',
			method: 'GET',
			data: { search: query },
			success: function(response) {
				const parser = new DOMParser();
				const doc = parser.parseFromString(response, 'text/html');
				const newServices = $(doc).find('#serviceContainer').children();

				// Animate out existing items
				$('#serviceContainer').children().each(function() {
					$(this).addClass('filtering-out');
				});

				// After animation, update content
				setTimeout(() => {
					$('#serviceContainer').empty();

					// Add new items with animation
					newServices.each(function(index) {
						const item = $(this).addClass('filtering-in');
						setTimeout(() => {
							$('#serviceContainer').append(item);
						}, index * 100); // Stagger the animations
					});

					// Remove loading state
					serviceGrid.removeClass('loading');

					// Update load more button
					updateLoadMoreButton();
				}, 400);
			},
			error: function(xhr, status, error) {
				console.error('Search failed:', error);
				serviceGrid.removeClass('loading');
			}
		});
	}

	function updateLoadMoreButton() {
		const hiddenServices = $('.hidden-service').length;
		$('#loadMoreBtn').toggle(hiddenServices > 0);
	}
});