$(document).ready(function() {
	let searchTimeout;
	const searchInput = $('input[name="search"]');
	const teamGrid = $('.team-grid');

	// Initialize load more button
	updateLoadMoreButton();

	searchInput.on('input', function() {
		clearTimeout(searchTimeout);
		teamGrid.addClass('loading');

		searchTimeout = setTimeout(() => {
			const query = $(this).val().trim();
			performSearch(query);
		}, 300);
	});

	function performSearch(query) {
		$.ajax({
			url: '/about',
			method: 'GET',
			data: {
				search: query,
				cp: 1
			},
			success: function(response) {
				const parser = new DOMParser();
				const doc = parser.parseFromString(response, 'text/html');

				// Get the entire team section to update
				const newTeamSection = $(doc).find('.team');

				// Animate out existing items
				$('#teamContainer').children().each(function() {
					$(this).addClass('filtering-out');
				});

				// After animation, update the entire team section
				setTimeout(() => {
					$('.team').replaceWith(newTeamSection);

					// Reapply animations to new items
					$('#teamContainer').children().each(function(index) {
						const item = $(this);
						setTimeout(() => {
							item.addClass('filtering-in');
						}, index * 100);
					});

					// Update URL without page refresh
					const newUrl = updateURLParameter(window.location.href, 'search', query);
					window.history.pushState({ path: newUrl }, '', newUrl);

					// Reinitialize event listeners
					initializeEventListeners();

					teamGrid.removeClass('loading');
				}, 400);
			},
			error: function(xhr, status, error) {
				console.error('Search failed:', error);
				teamGrid.removeClass('loading');
			}
		});
	}

	function initializeEventListeners() {
		$('input[name="search"]').off('input').on('input', function() {
			clearTimeout(searchTimeout);
			teamGrid.addClass('loading');

			searchTimeout = setTimeout(() => {
				const query = $(this).val().trim();
				performSearch(query);
			}, 300);
		});
		updateLoadMoreButton();
	}

	function updateLoadMoreButton() {
		const hiddenMembers = $('.hidden-team').length;
		$('#loadMoreTeam').toggle(hiddenMembers > 0);
	}

	// Helper function to update URL parameters
	function updateURLParameter(url, param, value) {
		const regex = new RegExp('([?&])' + param + '=.*?(&|$)', 'i');
		const separator = url.indexOf('?') !== -1 ? '&' : '?';

		if (url.match(regex)) {
			return url.replace(regex, '$1' + param + '=' + value + '$2');
		} else {
			return url + separator + param + '=' + value;
		}
	}
});

// Load More
function toggleTeamMembers() {
	const currentPage = parseInt($('[name="cp"]').val() || 1);
	const nextPage = currentPage + 1;

	$.ajax({
		url: '/about',
		method: 'GET',
		data: {
			cp: nextPage,
			search: $('input[name="search"]').val() || ''
		},
		success: function(response) {
			const parser = new DOMParser();
			const doc = parser.parseFromString(response, 'text/html');
			const newMembers = $(doc).find('#teamContainer').children();

			// Append new members with animation
			newMembers.each(function(index) {
				const item = $(this).addClass('filtering-in');
				setTimeout(() => {
					$('#teamContainer').append(item);
				}, index * 100);
			});

			// Update URL
			const newUrl = updateURLParameter(window.location.href, 'cp', nextPage);
			window.history.pushState({ path: newUrl }, '', newUrl);

			// Update or hide load more button
			if (newMembers.length < 4) {
				$('#loadMoreTeam').hide();
			}
		},
		error: function(xhr, status, error) {
			console.error('Load more failed:', error);
		}
	});
}