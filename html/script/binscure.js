const carousels = document.querySelectorAll("carousel");

carousels.forEach((carousel) => {
	const carouselElements = carousel.querySelector("div.carousel-items").children;
	const btnElements = carousel.querySelector("div.carousel-btns").children;
	
	const switchCarousel = (n) => {
		console.log("Switch to " + n);
		
		for (let i = 0; i < carouselElements.length; i++) {
			const child = carouselElements[i];
			const btn = btnElements[i];
			child.classList.remove("active");
			btn.classList.remove("active");
			if (i === n) {
				child.classList.add("active");
				btn.classList.add("active");
			}
		}
	};
	for (let i = 0; i < btnElements.length; i++) {
		const child = btnElements[i];
		if (child.tagName === "BUTTON") {
			child.onclick = () => {
				switchCarousel(i);
			};
		}
	}
});
for (let carousel in carousels) {

}

