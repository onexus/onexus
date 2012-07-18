function initCodeMirror( componentId ) {
            var txtArea = document.getElementById(componentId);

            var alertOnChange = function(cm, change) {

            if (change.text == '<') {
            CodeMirror.simpleHint(cm, CodeMirror.xmlHint);
            }

            txtArea.onchange();
            };

            var foldFunc = CodeMirror.newFoldFunction(CodeMirror.tagRangeFinder);

            var editor = CodeMirror.fromTextArea(txtArea, {
            mode : "application/xml",
            lineNumbers : true,
            lineWrapping: true,
            onGutterClick: foldFunc,
            onChange : alertOnChange,
            readOnly : true,
            onCursorActivity : function() {
            editor.setLineClass(hlLine, null);
            hlLine = editor.setLineClass(editor.getCursor().line,
            "activeline");
            },
            extraKeys: {"Ctrl-Space": function(cm) {CodeMirror.simpleHint(cm, CodeMirror.xmlHint);}}
            });
            var hlLine = editor.setLineClass(0, "activeline");
}