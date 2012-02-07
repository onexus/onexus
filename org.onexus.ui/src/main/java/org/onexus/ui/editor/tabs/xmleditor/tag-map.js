var tagMap = {};

tagMap[''] = [ 
	'<browser>\n</browser>' 
];
  
tagMap['browser'] = [
	'<defaultStatus/>', '<tabs>\n  </tabs>'
];

tagMap['tabs'] = [
	'<tab-topleft>\n    </tab-topleft>'
];

tagMap['tab-topleft'] = [
	'<id>[tab-id]</id>',
	'<title>[tab title]</title>',
	'<widgets>\n      </widgets>'
	];
	
tagMap['widgets'] = [
	'<widget-bookmark>\n          <id>bookmark</id>\n          <region>left</region>\n        </widget-bookmark>',
	'<widget-export>\n          <id>export</id>\n          <region>left</region>\n        </widget-export>'
	];
      


