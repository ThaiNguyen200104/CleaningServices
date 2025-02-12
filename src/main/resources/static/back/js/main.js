(function() {

	// Preloader
	const preloader = document.querySelectorAll('#preloader');
	window.addEventListener('load', function() {
		if (preloader.length) {
			this.document.getElementById('preloader').style.display = 'none'
		}
	});

	// Add Box Shadow in Header on Scroll
	window.addEventListener('scroll', function() {
		const header = document.querySelector('.header');
		if (window.scrollY > 0) {
			header.style.boxShadow = '0px 0px 30px 0px rgba(200, 208, 216, 0.30)'
		} else {
			header.style.boxShadow = 'none'
		}
	});

	// Sidebar toogle
	const sidebarNavWrapper = document.querySelector(".sidebar-nav-wrapper");
	const mainWrapper = document.querySelector(".main-wrapper");
	const menuToggleButton = document.querySelector("#menu-toggle");
	const menuToggleButtonIcon = document.querySelector("#menu-toggle i");
	const overlay = document.querySelector(".overlay");
	menuToggleButton.addEventListener("click", () => {
		sidebarNavWrapper.classList.toggle("active");
		overlay.classList.add("active");
		mainWrapper.classList.toggle("active");
		if (document.body.clientWidth > 1200) {
			if (menuToggleButtonIcon.classList.contains("lni-chevron-left")) {
				menuToggleButtonIcon.classList.remove("lni-chevron-left");
				menuToggleButtonIcon.classList.add("lni-menu");
			} else {
				menuToggleButtonIcon.classList.remove("lni-menu");
				menuToggleButtonIcon.classList.add("lni-chevron-left");
			}
		} else {
			if (menuToggleButtonIcon.classList.contains("lni-chevron-left")) {
				menuToggleButtonIcon.classList.remove("lni-chevron-left");
				menuToggleButtonIcon.classList.add("lni-menu");
			}
		}
	});
	overlay.addEventListener("click", () => {
		sidebarNavWrapper.classList.remove("active");
		overlay.classList.remove("active");
		mainWrapper.classList.remove("active");
	});

	// Popover Button
	document.addEventListener('DOMContentLoaded', function() {
		var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
		var popoverList = popoverTriggerList.map(function(popoverTriggerEl) {
			return new bootstrap.Popover(popoverTriggerEl, {
				html: true,
				trigger: 'click',
				placement: 'top'
			});
		});
	});

})();

setTimeout(function() {
	document.querySelector('.error-message').innerText = '';
}, 6000);

// Count messages_length
document.getElementById('description').addEventListener('input', function() {
	const messageLength = this.value.length;
	document.getElementById('charCount').innerText = `${messageLength}/255`;
});

document.addEventListener("DOMContentLoaded", function() {
	var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
	var tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
		return new bootstrap.Tooltip(tooltipTriggerEl);
	});
});

