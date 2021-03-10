/*const purchase = (item) => {
	let btn = document.getElementById("purchase-btn");
	btn.innerText = "Loading...";
	
	const data = {
		product: item
	};
	const prom = req('/payments/new', data);
	prom.then(response => {
		return response.text();
	}).then(data => {
		btn.innerText = "PURCHASE";
		console.log(data);
		if (!data.startsWith("ok ")) {
			if (data === "loggedout") {
				const params = new URL(document.location.href).searchParams;
				if (params.has("purchase")) {
					goto("/login?redir=" + encodeURIComponent(window.location.href));
				} else {
					goto("/login?redir=" + encodeURIComponent(window.location.href + "?purchase"));
				}
			} else {
				alert(data);
			}
		} else {
			let invoiceId = data.substring("ok ".length).trim();
			window.btcpay.showInvoice(invoiceId);
		}
	})
	.catch(err => {
		btn.innerText = "PURCHASE";
		alert(err);
	});
	
	prom.catch(err => {
		btn.innerText = "PURCHASE";
		alert(err);
	});
};*/
document.onModalOpen.push((modal) => {
	const buyText = document.querySelector("#buy-text");
	buyText.innerHTML = `
			This product is not available for online purchase at this time.<br>
			To purchase please contact the developers either at <a href="mailto:x4e_x4e@protonmail.com" style="text-decoration: none;">x4e_x4e@protonmail.com</a>, <a href="/discord">Discord</a> or <a href="https://app.element.io/#/group/+binclub:matrix.org">Element</a>.<br>
			All major cryptocurrencies are supported.
	`;
});
