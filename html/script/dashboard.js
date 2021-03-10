const download = (id) => {
	//const password = prompt("Please confirm your password");
	download0(id, "");
};

const download0 = (id, password) => {
	const form = document.createElement("form");
	form.style.display = "none";
	form.action = "/download.jar";
	form.method = "post";
	form.target = "_blank";
	
	const idInput = document.createElement("input");
	idInput.type = "text";
	idInput.name = "id";
	idInput.value = id.toString();
	form.appendChild(idInput);
	
	const passInput = document.createElement("input");
	passInput.type = "password";
	passInput.name = "password";
	passInput.value = password;
	form.appendChild(passInput);
	
	document.body.appendChild(form);
	form.submit();
	document.body.removeChild(form);
};

