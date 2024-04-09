function sendMessage() {
  let question = document.getElementById('question').value;
  let xhr = new XMLHttpRequest();
  xhr.open('POST', '/chat.html', true);
  xhr.setRequestHeader('Content-Type', 'text/plain');
  xhr.onreadystatechange = function () {
	  if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
	    location.reload();
	  }
  };
  xhr.send(question);		
}
function insert() {
  let xhr = new XMLHttpRequest();
  xhr.open('POST', '/insert.html', true);
  xhr.setRequestHeader('Content-Type', 'text/plain');
  xhr.send();		
}