function toggleCheckBoxes(elem, tableId) {

    var div = document.getElementById(tableId);
    var chk = div.getElementsByTagName('input');
    var len = chk.length;

    for (var i = 0; i < len; i++) {
        if (chk[i].type === 'checkbox') {
            chk[i].checked = elem.checked;
        }
    }        
    
    updateSelected(tableId);
        
}

function updateSelected(tableId) {

	values = "";
	
	var table = document.getElementById(tableId);
	var widget = document.getElementById("rows-selection-container");
    var chk = table.getElementsByTagName('input');
    var len = chk.length;

    for (var i = 0; i < len; i++) {
        if (chk[i].type === 'checkbox') {
            if (chk[i].checked) {
            	values += chk[i].value + ':::';
            }
        }
    }  

	 widget.value = values;
}

function clearSelected() {
	var chk = document.getElementsByTagName('input');
   var len = chk.length;

   for (var i = 0; i < len; i++) {
       if (chk[i].type === 'checkbox' && chk[i].className == "sc")  {
           chk[i].checked = false;
       }
    }  
	 
}

