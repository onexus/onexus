 window.onload = setupFunc;

 function setupFunc() {
   document.getElementsByTagName('body')[0].onclick = clickFunc;
   hideBusysign();
   Wicket.Ajax.registerPreCallHandler(showBusysign);
   Wicket.Ajax.registerPostCallHandler(hideBusysign);
   Wicket.Ajax.registerFailureHandler(hideBusysign);
 }

 function hideBusysign() {
   document.getElementById('bysy_indicator').style.display ='none';
 }

 function showBusysign() {
   document.getElementById('bysy_indicator').style.display ='inline';
 }

 function clickFunc(eventData) {
   var clickedElement = (window.event) ? event.srcElement : eventData.target;
   if (clickedElement.tagName.toUpperCase() == 'BUTTON' || clickedElement.tagName.toUpperCase() == 'A' || clickedElement.parentNode.tagName.toUpperCase() == 'A'
     || (clickedElement.tagName.toUpperCase() == 'INPUT' && (clickedElement.type.toUpperCase() == 'BUTTON' || clickedElement.type.toUpperCase() == 'SUBMIT'))) {
     showBusysign();
   }
 }
