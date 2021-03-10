window.onload = () => {
	const speed = 12;
	
	let el = document.getElementById("about-text");
	let insertEl = document.getElementById("insert-text");
	let text = el.innerHTML;
	
	let progress = 0;
	const add = () => {
		if (progress < text.length) {
			el.innerHTML = el.innerHTML.substr(1);
			insertEl.innerHTML += text.charAt(progress);
			progress += 1;
			setTimeout(add, speed)
		}
	};
	add()
};
