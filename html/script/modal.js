if (document.onModalOpen === undefined) {
	document.onModalOpen = [];
}
if (document.onModalFound === undefined) {
	document.onModalFound = [];
}
if (document.modals === undefined) {
	document.modals = [];
}

let modalN = 0;
document.querySelectorAll(".modal").forEach((modal) => {
	let modalOpen = null;
	document.querySelectorAll(".modal-open").forEach((btn) => {
		if (btn.attributes.modal.value === "" + modalN) {
			modalOpen = btn;
		}
	});
	modalOpen.onclick = () => {
		document.onModalOpen.forEach((callback) => {
			callback(modal);
		});
		
		modal.style.display = "grid";
	}
	modal.open = modalOpen.onclick;
	
	let modalClose = null;
	document.querySelectorAll(".modal-close").forEach((btn) => {
		if (btn.attributes.modal.value === "" + modalN) {
			modalClose = btn;
		}
	});
	if (modalClose != null) {
		modalClose.onclick = () => {
			modal.style.display = "none";
		}
	}
	
	window.onclick = (event) => {
		if (event.target === modal) {
			modal.style.display = "none";
		}
	}
	
	modalN += 1;
	
	document.modals.push(modal);
	document.onModalFound.forEach((callback) => {
		callback(modal);
	});
});

