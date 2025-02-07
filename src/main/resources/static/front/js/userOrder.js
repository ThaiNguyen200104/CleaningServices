function cancel(event) {
	var item = event.target;
	var requestId = item.getAttribute("requestId");
	var staffId = item.getAttribute("staffId");
	var url = staffId && staffId !== '0'
		? `/user/cancelOrder?requestId=${requestId}&staffId=${staffId}`
		: `/user/cancelOrder?requestId=${requestId}`;
	if (confirm("Are you sure you want to cancel this request?")) {
		fetch(url, {
			method: 'GET'
		}).then(response => {
			if (response.redirected) {
				window.location.href = response.url;
			} else {
				return response.text();
			}
		}).then(data => {
			if (data) {
				alert("Failed to cancel the request.");
			}
		}).catch(error => {
			console.error('Error:', error);
			alert("An error occurred while canceling the order.");
		});
	}
}


function confirmAction(event) {
	var item = event.target;
	var requestId = item.getAttribute("urdId");
	var serId = item.getAttribute("serId");
	var startDate = item.getAttribute("startDate");
	var price = item.getAttribute("price");
	var staffId = item.getAttribute("staffId");
	var date = new Date(startDate);
	var formattedDate = date.getFullYear() + '-' +
		String(date.getMonth() + 1).padStart(2, '0') + '-' +
		String(date.getDate()).padStart(2, '0');

	if (confirm("Are you sure you want to confirm this request?")) {
		const formData = new FormData();
		formData.append('urdId', requestId);
		formData.append('serId', serId);
		formData.append('startDate', formattedDate);
		formData.append('price', price);
		formData.append('staffId', staffId);

		fetch('/user/confirmOrder', {
			method: 'POST',
			body: formData
		}).then(response => {
			if (response.redirected) {
				window.location.href = response.url;
			} else {
				return response.text();
			}
		}).then(data => {
			if (data) {
				alert("Failed to confirm the order");
			}
		}).catch(error => {
			console.log(error);
		});
	}
}