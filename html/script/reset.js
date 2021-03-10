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
const currentPassInput = document.getElementById("password-input");
const newPassInput = document.getElementById("new-password-input");
const resetBtn = document.getElementById("change-btn");

const verifyInputs = (alert = false) => {
	if (!currentPassInput.validity.valid) {
		if (alert) {
			displayMessage("err", "Please fill in a valid current password");
		}
		return false;
	}
	if (!newPassInput.validity.valid) {
		if (alert) {
			displayMessage("err", "Please fill in a valid new password");
		}
		return false;
	}
	return true;
};

const postReset = () => {
	if (verifyInputs(true)) {
		form.action = "/reset";
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
