const goto = (href) => {
	window.location.href = href;
};

const refresh = (href) => {
	window.location.reload();
};

function jsonToFormData(data) {
	const formData = new FormData();
	Object.keys(data).forEach(key => {
		formData.append(key, data[key]);
	});
	return formData;
}

const req = (url, data, opts = {
	method: 'POST',
	mode: 'no-cors', // no-cors, *cors, same-origin
	credentials: 'same-origin', // include, *same-origin, omit
	cache: 'no-cache',
	headers: {
		'Content-Type': 'application/xwww.form-urlencoded'
	}
}) => {
	opts.body = JSON.stringify(data);
	console.log(opts.body);
	return fetch(url, opts);
}
