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