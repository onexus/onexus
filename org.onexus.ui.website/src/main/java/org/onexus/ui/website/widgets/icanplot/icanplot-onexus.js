
function supports_canvas() {
    return !!document.createElement('canvas').getContext;
    return false;
}

function icanplot_init(url, default_cols) {
    viz.init(document.getElementById("plot-area"));

    viz.minPointSize = 2;
    viz.maxPointSize = 10;

    // setting this allows users to select points by defining an arbitrary polygon
    viz.selectionType = 'rectangle';
    viz.selectionActive = true;

    // hooking the function to be called when points are selected
    viz.processSelectedData = processSelectedData;

    data.loadData({
        url: url,
        filetype: 'tsv',
        header: true
    });

    if (data.success) {
        drawFig(default_cols[0], default_cols[1], default_cols[2], default_cols[3]);
    }
}

function drawFig(x_axs, y_axs, color, size){

    // read all the data columns
    cols = data.getCols(x_axs, y_axs, color, size);

    viz.xAxisLabel = data.colNames[x_axs];
    viz.yAxisLabel = data.colNames[y_axs];
    if(color != -1) {
        viz.colorLabel = data.colNames[color];
    }
    if(size != -1) {
        viz.sizeLabel = data.colNames[size];
    }

    viz.plot({
        x: cols.x,
        y: cols.y,
        color: cols.color,
        size: cols.size,
        label: data.rowNames
    });
}

function redrawFig() {
    var x_axs = document.getElementById('x-select').value;
    var y_axs = document.getElementById('y-select').value;
    var color = document.getElementById('color-select').value;
    var size = document.getElementById('size-select').value;
    drawFig(x_axs, y_axs, color, size);
}

function processSelectedData(col1, col2, col3) {
    alert("Name: " + col1 + "\n X: " + col2 + "\n Y: " + col3 );
}


