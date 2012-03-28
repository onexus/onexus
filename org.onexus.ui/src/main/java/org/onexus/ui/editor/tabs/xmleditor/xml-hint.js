(function () {
	
  
  
  CodeMirror.xmlHint = function(editor) {
    
    var cur = editor.getCursor();
    var token = editor.getTokenAt(cur);

	 var parent = token;
	 
	 var iniFrom = {line: cur.line, ch: cur.ch};
	 
	 if (token.className == 'tag') {
	 	  iniFrom.ch = token.start;
	 }
	 
	 var iniTo = {line: cur.line, ch: token.end};
	 	 
	 var notOpen = [];
	 
    // Look for parent tag
  	 cur.ch = parent.start;
    lineLoop:
    for(var l = cur.line; l >= 0; l--) {
    	cur.line = l;
    	for(var i= cur.ch - 1; i >= 0; i--) {
    	   cur.ch = i;	
    		var newToken = editor.getTokenAt(cur);
    		
    		// Skip non-tags
    		if (newToken.className != 'tag') {
    			i = newToken.start;
    			continue;	
    		}

			// Skip closed tags
			if ( newToken.string.indexOf('/') >= 0) {
				
				// Autoclosed tag
				if (newToken.string == '/>') {
					cur.ch = newToken.start - 1;
					newToken = editor.getTokenAt(cur);
				} else {
					tag = newToken.string.replace('\/', '');
					notOpen.push(tag);	
				}
				
				i = newToken.start;
				continue;
			}    		

			// Skip pending to open tags
			if (newToken.string == notOpen.pop()) {
				i = newToken.start;
				continue;	
			}    		
    		
    		parent = newToken;
    		break lineLoop;	
    		
    	};
    	if (l > 0) {
    		cur.ch = editor.getLine(l - 1).length;	
    	}
    };     
    
    var strToken = token.string.replace('<', '');
    var strParent = parent.string.replace('<', '');
                
    return {
    	list: getCompletions(strToken, strParent, iniFrom.ch),
      from: iniFrom,
      to: iniTo
    };
  };
    
  function getCompletions(token, parent, indent) {

     var spaces = "\n";
     for (var i=0; i < indent; i++) {
        spaces += " ";
     };

     var tags = tagMap[parent];
     var completions = [];

     for (var i=0; i < tags.length; i++) {
        completions[i] = tags[i].replace(/\n/g, spaces );
     }

  	 return completions;
  }

})();