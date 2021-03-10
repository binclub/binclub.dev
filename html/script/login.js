const statusArea = document.getElementsByClassName("status-area")[0];
const params = new URL(document.location.href).searchParams;

const displayMessage = (type, msg) => {
	statusArea.classList.value = "status-area";
	statusArea.classList.add(type);
	const p = document.createElement("p");
	p.textContent = msg;
	statusArea.innerHTML = "";
	statusArea.appendChild(p);
};

if (params.has("type")) {
	displayMessage(decodeURIComponent(params.get("type")), decodeURIComponent(params.get("msg")));
	
	window.history.replaceState(null, null, window.location.pathname);
}


const form = document.getElementById("login-form");
const emailInput = document.getElementById("email-input");
const passwordInput = document.getElementById("password-input");
//const tocInput = document.getElementById("toc-input");
const loginBtn = document.getElementById("login-btn");
const signupBtn = document.getElementById("signup-btn");

if (params.has("redir")) {
	document.getElementById("redir-input").value = params.get("redir");
}

const verifyInputs = (alert = false) => {
	if (!emailInput.validity.valid) {
		if (alert) {
			displayMessage("err", "Please fill in a valid email address");
		}
		return false;
	}
	if (!passwordInput.validity.valid) {
		if (alert) {
			displayMessage("err", "Please fill in a valid password");
		}
		return false;
	}
	//if (!tocInput.checked) {
	//	if (alert) {
	//		displayMessage("err", "Please accept the terms and conditions");
	//	}
	//	return false;
	//}
	return true;
};

const postLogin = () => {
	if (verifyInputs(true)) {
		form.action = "/login";
		grecaptcha.execute();
	}
};

const postSignup = () => {
	if (verifyInputs(true)) {
		form.action = "/register";
		grecaptcha.execute();
	}
};

window.postCaptcha = () => {
	if (verifyInputs()) {
		form.submit();
	}
};

const updateBtns = () => {
	const disabled = !verifyInputs();
	//loginBtn.disabled = disabled;
	//signupBtn.disabled = disabled;
};

updateBtns();
