function getListings(){
	const http = new XMLHttpRequest();
	http.open("GET", "/listing/list", false);
	http.send();
	if(http.status == 200){
		return JSON.parse(http.responseText);
	}
}

function getListing(title){
	const http = new XMLHttpRequest();
	http.open("GET", "/listing/" + title, false);
	http.send();
	if(http.status == 200){
		return JSON.parse(http.responseText);
	}
}

function getCurrentUser(){
	const http = new XMLHttpRequest();
	http.open("GET", "/username", false);
	http.send();
	if(http.status == 200){
		return http.responseText;
	}
}

function getFullName(email){
	const http = new XMLHttpRequest();
	http.open("GET", "/fullName" + email, false);
	http.send();
	if(http.status == 200){
		return http.responseText;
	}
}

function search(tag){
	const http = new XMLHttpRequest();
	http.open("GET", "/search"+ tag , false);
	http.send();
	if(http.status == 200){
		return JSON.parse(http.responseText);
	}
}

function getTags(listing){
	const http = new XMLHttpRequest();
	http.open("GET", "/tags"+ listing , false);
	http.send();
	if(http.status == 200){
		return http.responseText;
	}
}

function getSeller(email){
	const http = new XMLHttpRequest();
	http.open("GET", "/seller"+ email, false);
	http.send();
	if(http.status == 200){
		return JSON.parse(http.responseText);
	}
}

function getReviews(email){
	const http = new XMLHttpRequest();
	http.open("GET", "/reviews"+ email, false);
	http.send();
	if(http.status == 200){
		return JSON.parse(http.responseText);
	}
}

function getReview(title){
	const http = new XMLHttpRequest();
	http.open("GET", "/review"+ title, false);
	http.send();
	if(http.status == 200){
		return JSON.parse(http.responseText);
	}
}

