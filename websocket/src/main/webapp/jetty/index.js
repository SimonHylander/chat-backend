var ws = new WebSocket("ws://127.0.0.1:8080");

ws.onopen = function() {
    console.log("Opened!");
};

ws.onmessage = function (evt) {
    var jsonData = $.parseJSON(evt.data);
    console.log("onmessage");
    console.log(jsonData);
	
	if(jsonData.data.length > 0) {
		var tbody = $('#item-table').find('tbody');
		tbody.html("");
		$.each(jsonData.data, function(index, item)  {
			console.log(item);
			tbody.append(
				'<tr><td>'+item.id+'</td><td>'+item.name+'</td><td>'+item.description+'</td></tr>'
	    	);
		});
	}
};

ws.onclose = function() {
    console.log("Closed!");
};

ws.onerror = function(err) {
    console.log("Error: "+ err);
};

$(function(){
	$('#add-item-btn').click(function(e) {
		e.preventDefault();
		var name = $('#item-name').val();
		var descr = $('#item-description').val();
		
		ws.send(JSON.stringify({
			action: 'add',
			name: name,
			description: descr
		}));
	});
});