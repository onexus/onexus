//Array.max = function(array){
//	return Math.max.apply(Math, array);
//};

//Array.min = function(array){
//	return Math.min.apply(Math, array);
//};

// not used
Point = {
	x: 0,
	y: 0
}

// for all plotting
viz = {
	pad: 20,
	padTop :	20,
	padRight:	20,
	padBottom:	20,
	padLeft:	75,
	padLegend:  60,
	gutter: 10,

	defaultPointSize: 2,
	minPointSize:	 2,
	maxPointSize:	10,
	
	alpha: 0.7,

	ticsFont:	'Arial',
	ticsSize:	10,
	axisLabelFont:	'Arial',
	axisLabelSize:	13,

	textColor: 'rgb(51, 51, 51)',
	colorLight: 'rgba(102, 102, 102, 1)',
	colorDark: 'rgba(51, 51, 51, 1)',

	showLegend: true,

	threshColor: true,
	threshSize: false,

	colorBarWidth: 100,
	colorBarHeight: 20,
	sizeBarWidth: 100,
	sizeBarHeight: 20,

	// REMOVE
	//axisLabelFont:	"18pt Arial",
	//ticsFont:	"15pt Arial",

	scaleX: 1,
	scaleY: 1,
	transX: 0,
	transY: 0,

	useColor: false,
	useSize: false,

	plotX:	[],
	plotY:	[],
	plotColor:	[],
	plotSize:	[],
	plotLabels: [],
	highlight: new Array(),

	xAxisLabel: 'X',
	yAxisLabel: 'Y',
	colorLabel: 'Color',
	sizeLabel: 'Size',

	// vars for dealing with selection of image
	selectionType: 'none',	// other values, polygon, rectangle
	selectionActive: false,
	selX: new Array(),
	selY: new Array(),

	imgSaved: false,
	img: new Image(),

	processSelectedData: function() {
		//alert('nothing to do');
		return;
	},

	init: function(cnv) {
		//alert(cnv);
		//alert(this.selectionType);
		//alert(this.selectionActive);
		//alert(this.imgSaved);

		this.canvas = cnv;
		this.baseWidth = this.canvas.width;
		this.baseHeight = this.canvas.height;

		this.width = this.canvas.width;
		this.height = this.canvas.height;

		if (this.canvas.getContext) {
			this.ctx = this.canvas.getContext('2d');
		}

		this.canvas.addEventListener('mousedown', this.mousedown, false);
		this.canvas.addEventListener('mouseup', this.mouseup, false);
		this.canvas.addEventListener('mousemove', this.mousemove, false);
		this.canvas.addEventListener('mouseout', this.mouseout, false);
		this.canvas.addEventListener('dblclick', this.dblclick, false);

		this.defaultColor = this.formRGBcolor(0, 63, 255, 0.5);
	},

	// set appropriate paddings based on the choices
	setPaddings: function() {
		this.padBottom = this.pad + this.gutter + this.ticsSize + this.gutter + this.axisLabelSize;

		if(this.showLegend && (this.customColor || this.customSize)) {
			this.legendHeight = 
				  this.gutter / 2 + this.axisLabelSize
				+ this.gutter / 2 + this.colorBarHeight
				+ this.gutter / 2 + this.ticsSize 
				+ this.gutter;
			this.padLegend = this.legendHeight + this.gutter;
			this.padBottom += this.padLegend;

			this.canvas.height = this.baseHeight + this.padLegend;
		} else {
			this.canvas.height = this.baseHeight;
		}
		this.height = this.canvas.height;
	},

	// find min and max of each of the 4 dimensions
	getDataRange: function() {
		if(this.threshColor && this.customColor) {
			this.thresholdArr(this.dataColor, 0.01, 0.99);
		}

		if(this.threshSize && this.customSize) {
			this.thresholdArr(this.dataSize, 0.01, 0.99);
		}

		//this.minX = this.dataX.min();
		//this.maxX = this.dataX.max();
		//this.minY = this.dataY.min();
		//this.maxY = this.dataY.max();
		//this.minColor = this.dataColor.min();
		//this.maxColor = this.dataColor.max();
		//this.minSize = this.dataSize.min();
		//this.maxSize = this.dataSize.max();

		this.minX = Math.min.apply(null, this.dataX);
		this.maxX = Math.max.apply(null, this.dataX);
		this.minY = Math.min.apply(null, this.dataY);
		this.maxY = Math.max.apply(null, this.dataY);

		this.minColor = Math.min.apply(null, this.dataColor);
		this.maxColor = Math.max.apply(null, this.dataColor);

		//alert(this.maxColor);

		this.minSize = Math.min.apply(null, this.dataSize);
		this.maxSize = Math.max.apply(null, this.dataSize);

		// leave a buffer between bounding box and points
		var buffer = 0.05;

		// TODO: check for positive/negative values
		var xrange = this.maxX - this.minX;
		this.minX -= xrange * buffer;
		this.maxX += xrange * buffer;

		var yrange = this.maxY - this.minY;
		this.minY -= yrange * buffer;
		this.maxY += yrange * buffer;

		// REMOVE
		//this.minX = 0;
		//this.minY = 0;
		//this.maxY = 2.5;
	},

	thresholdArr: function(arr, lowPer, highPer) {
		var vals = arr.slice();
		function sortNumber(a, b) { return a - b; }
		vals.sort(sortNumber);

		//alert(vals);

		var idxLow = Math.round(arr.length * lowPer);
		var idxHigh = Math.round(arr.length * highPer) - 1;

		//alert(this.dataColor.length + ',' + idx_low + ',' + idx_high);
		//alert(this.minColor + ',' + this.maxColor);

		var valMin = vals[idxLow];
		var valMax = vals[idxHigh];

		//alert(valMin + ',' + valMax);

		for(var i = 0; i < arr.length; i++) {
			if (arr[i] < valMin) {
				arr[i] = valMin;
			} else if(arr[i] > valMax) {
				arr[i] = valMax;
			}
		}
	},

	// get constants to transform data values to coordinates
	// based on size of canvas element
	setTransformation: function() {
		var innerMargin = 10;
		this.scaleX = (this.width - this.padRight - this.padLeft) /
								(this.maxX - this.minX);
		this.scaleY = (this.height - this.padTop - this.padBottom) /
								(this.maxY - this.minY);
		//alert(scaleX);
		//alert(scaleY);
	
		this.transX = this.padLeft - this.minX * this.scaleX;
		this.transY = -this.height + this.padBottom - this.minY * this.scaleY;
		
		//startTime = getCurTime();

		var rangeColor = this.maxColor - this.minColor;
		var rangeSize = this.maxSize - this.minSize;
		var aColor = new Array(3);


		this.plotX = [];
		this.plotY = [];
		this.plotColor = [];
		this.plotSize = [];
		for(var i = 0; i < this.dataX.length; i++) {
			this.plotX.push(this.warpX(this.dataX[i]));
			this.plotY.push(this.warpY(this.dataY[i]));

			if(this.dataColor.length > 0) {
				var colorFrac = (this.dataColor[i] - this.minColor) / rangeColor;
				//aColor = this.getColor(colorFrac, 31, 191);	// sober
				aColor = this.getColor(colorFrac, 64, 239);	// bright

				this.plotColor.push(this.formRGBcolor(aColor["red"], aColor["green"], aColor["blue"], 0.7));
			} else {
				this.plotColor.push(this.defaultColor);
			}

			if(this.dataSize.length > 0) {
				var sizeFrac = (this.dataSize[i] - this.minSize) / rangeSize;
				sizep = sizeFrac * (this.maxPointSize - this.minPointSize) + this.minPointSize;
				this.plotSize.push(sizep);
			} else {
				this.plotSize.push(this.defaultPointSize);
			}
		}

		//alert("time to transform: " + (getCurTime() - startTime)/1000);
	
		//this.ctx.translate(0, -this.height);
		//this.ctx.transform(1, 0, 0, -1, 0, 0);
		//this.ctx.translate(transX, transY);
		//this.ctx.scale(scaleX, scaleY);
	},

	formRGBcolor: function(red, green, blue, alpha) {
		var color = 'rgba(' + red + ',' + green + ',' + blue + ',' + alpha + ')';
		return color;
	},

	getColor: function (val, low, high) {
		// between 0 and 4/6 coz we only want a range AND from blue to red
		val = 0.66667 * (1 - val)
	
		var colors = new Array(3);
		colors["red"] = this.getColorLevel(val, "red", low, high);
		colors["green"] = this.getColorLevel(val, "green", low, high);
		colors["blue"] = this.getColorLevel(val, "blue", low, high);
	
		return colors;
	},

	getColorLevel: function(val, color, low, high) {
		var x0 = new Array(3);
		x0["green"] = 0 / 6;
		x0["blue"] = 2 / 6;
		x0["red"] = 4 / 6;
		val -= x0[color];
	
		// make val between 0 and 1
		while(val < 1) {
			val++;
		}
		while(val > 1) {
			val--;
		}

		var y;
		if(val >= 0 && val < 1/6) {
			y = 6 * val;
		} else if(val >= 1/6 && val < 3/6) {
			y = 1;
		} else if(val >= 3/6 && val < 4/6) {
			y = 6 * -val + 4;
		} else if(val >= 4/6 && val <= 6/6) {
			y = 0;
		}

		var amp = high - low;
		var y0 = low;
		y = y * amp + y0;
	
		return Math.round(y);
	},

	// functions to convert data values to coordinate and inverse
	warpX: function(x) {
		return (x * this.scaleX + this.transX);
	},

	warpY: function(y) {
		return -(y * this.scaleY + this.transY);
	},

	warpInvX: function(x) {
		return ((x - this.transX) / this.scaleX);
	},

	warpInvY: function(y) {
		return (-y - this.transY) / this.scaleY;
	},

	// draw the frame in which data will be plotted
	drawFrame: function () {
		var buffer = 0;

		// fill the rect white!
		this.ctx.fillStyle = "rgba(255, 255, 255, 1)";
		this.ctx.fillRect(0, 0, this.width, this.height);

		// boundary
		var x = 0 + this.padLeft - buffer;
		var y = 0 + this.padTop - buffer;
		var w = this.width - this.padLeft - this.padRight;
		var h = this.height - this.padTop - this.padBottom;

		this.ctx.lineWidth = 1;
		this.ctx.fillStyle = "rgba(0, 0, 0, 1)";
		this.ctx.strokeStyle = "rgba(0, 0, 0, 0.5)";

		this.crispStrokeRect(x, y, w, h);

		this.drawAxis();
		this.drawTics();
		this.drawAxisLabels();
		this.drawLegend();
	}, 

	// draw X and Y axis
	drawAxis: function() {
		this.ctx.lineWidth = 1;

		// axis if max and min are of different signs, ie, axis will be visible
		// x axis
		if(this.minY * this.maxY < 0) {
			x1 = this.warpX(this.minX);
			y1 = this.warpY(0);
			x2 = this.warpX(this.maxX);
			this.crispLine(x1, y1, x2, y1);
		}

		// y axis
		if(this.minX * this.maxX < 0) {
			y1 = this.warpY(this.minY);
			x1 = this.warpX(0);
			y2 = this.warpY(this.maxY);
			this.crispLine(x1, y1, x1, y2);
		}

		// 45deg line
		/*
		this.ctx.beginPath();
		a1 = this.warpX(Math.max(this.minX, this.minY));
		b1 = this.warpY(Math.max(this.minX, this.minY));
		a2 = this.warpX(Math.min(this.maxX, this.maxY));
		b2 = this.warpY(Math.min(this.maxX, this.maxY));
		this.ctx.moveTo(a1, b1);
		this.ctx.lineTo(a2, b2);
		this.ctx.stroke();
		this.ctx.closePath();
		*/
	},

	// draw tic marks
	drawTics: function() {
		var p, q;

		this.ctx.fillStyle = this.textColor;
		this.ctx.font = this.ticsSize + "pt " + this.ticsFont;
		this.ctx.lineWidth = 1;

		this.ctx.textBaseline = "top";
		this.ctx.textAlign = "center";

		var windowX = this.ticsWindowSize(this.minX, this.maxX);
		var startposX;
		if (this.minX > 0)
			startposX = this.minX + windowX - this.minX % windowX;
		else
			startposX = this.minX - this.minX % windowX;

		q = this.height - this.padBottom;
		for(var xp = startposX; xp < this.maxX; xp += windowX) {
			p = this.warpX(xp);
			this.crispLine(p, q, p, q + 5);

			this.ctx.fillStyle = this.textColor;
			this.ctx.fillText(xp, p - 1, q + this.gutter);
		}

		this.ctx.textBaseline = "middle";
		this.ctx.textAlign = "right";
		var windowY = this.ticsWindowSize(this.minY, this.maxY);
		var startposY;
		if (this.minY > 0)
			startposY = this.minY + windowY - this.minY % windowY;
		else
			startposY = this.minY - this.minY % windowY;

		p = this.padLeft;
		for(var yp = startposY; yp < this.maxY; yp += windowY) {
			q = this.warpY(yp);
			this.crispLine(p - 1, q, p - 1 - 5, q);
			this.ctx.fillText(yp, p - this.gutter, q - 1);
		}
	},

	// determine interval between tick marks
	ticsWindowSize: function(valMin, valMax) {
		var valRange = valMax - valMin;
		var windowSize = 10;
		var numWindow = 8;

		windowSize = Math.ceil(valRange / numWindow);
		power = Math.log(valRange) / Math.log(10);
		factor = Math.pow(10, (Math.floor(power) - 1)); 

		//windowSize = Math.round(valRange / factor);
		//if(windowSize % 5 != 0 && windowSize % 10 != 0)
		if(windowSize % factor != 0)
			windowSize = Math.ceil(windowSize / factor) * factor;
	
		return windowSize;
	},

	drawAxisLabels: function() {
		this.ctx.fillStyle = this.textColor;

		this.drawXAxisLabel();
		this.drawYAxisLabel();
	},

	drawXAxisLabel: function() {
		var xp = this.padLeft + (this.width - this.padLeft - this.padRight) / 2;
		var yp = this.height - this.padBottom + this.gutter * 2 + this.ticsSize;

		this.ctx.font = this.axisLabelSize + "pt " + this.axisLabelFont;
		this.ctx.textBaseline = "top";
		this.ctx.textAlign = "center";

		this.ctx.fillText(this.xAxisLabel, xp, yp);
	},

	drawYAxisLabel: function() {
		var xp = this.padLeft - 60;
		var yp = this.padTop + (this.height - this.padTop - this.padBottom) / 2;

		this.ctx.font = this.axisLabelSize + "pt " + this.axisLabelFont;
		this.ctx.textBaseline = "top";
		this.ctx.textAlign = "center";

		this.ctx.save();
		this.ctx.rotate(-Math.PI / 2);
		this.ctx.fillText(this.yAxisLabel, -yp, xp);
		this.ctx.restore();
	},

	// draw a legend
	drawLegend: function() {
		if(! this.showLegend || (! this.customColor && ! this.customSize)) {
			return;
		}

		//alert('draw legend');
		var x = this.padLeft;
		var w = this.width - this.padLeft - this.padRight;
		var y = this.height - this.padBottom + this.gutter * 3 + this.ticsSize + this.axisLabelSize;
		var h = this.legendHeight;
		this.ctx.strokeStyle = this.textColor;
		this.ctx.lineWidth = 1;
		//this.crispStrokeRect(x, y, w, h);

		if (this.customColor) {
			var widthUsed = this.drawColorLegend(x, y);
			x += widthUsed;
		}

		if (this.customSize) {
			this.drawSizeLegend(x, y);
		}
	},

	// draw the legend of color of the points
	drawColorLegend: function(x, y) {
		// get label names
		var step = (this.maxColor - this.minColor) / 2;
		var labels = Array();
		for(var i = 0; i < 3; i++) {
			var val = this.minColor + i * step
			var text = Math.round(val * 100) / 100;
			labels.push(text);
		}

		var label0Width = this.ctx.measureText(labels[0]).width;
		var labelnWidth = this.ctx.measureText(labels[3 - 1]).width;

		var drawX = x + label0Width / 2 + this.gutter;
		var drawY = y + this.gutter / 2;

		// draw label
		this.ctx.textBaseline = "top";
		this.ctx.textAlign = "left";
		this.ctx.font = this.axisLabelSize + "pt " + this.axisLabelFont;
		this.ctx.fillStyle = this.colorDark;
		this.ctx.fillText(this.colorLabel, drawX, drawY);

		drawY += this.axisLabelSize + this.gutter / 2;

		// draw color bar
		var aColor;
		var rgbColor;
		var stops = [0, 0.25, 0.5, 0.75, 1];
		var gradient = this.ctx.createLinearGradient(drawX, drawY, drawX + this.colorBarWidth, drawY + this.colorBarHeight);
		for(var i = 0; i < stops.length; i++) {
			var aColor = this.getColor(stops[i], 64, 239);	// bright
			var rgbColor = this.formRGBcolor(aColor["red"], aColor["green"], aColor["blue"], 0.7);
			gradient.addColorStop(stops[i], rgbColor);
		}
		this.ctx.fillStyle = gradient;
		this.ctx.fillRect(drawX, drawY, this.colorBarWidth,this.colorBarHeight);

		// draw tics values
		drawY += this.colorBarHeight + this.gutter / 2;
		this.ctx.font = this.ticsSize + "pt " + this.ticsFont;
		this.ctx.fillStyle = this.textColor;
		this.ctx.textBaseline = "top";
		this.ctx.textAlign = "center";
		for(var i = 0; i < 3; i++) {
			var xp = drawX + i * this.colorBarWidth / 2;
			this.ctx.fillText(labels[i], xp, drawY);
		}

		// bounding box
		var w = this.colorBarWidth + label0Width / 2 + labelnWidth / 2 + this.gutter * 2;

		this.ctx.strokeStyle = this.colorLight;
		this.crispStrokeRect(x, y, w, this.legendHeight);

		return w;
	},

	// draw the legend of the sizes
	drawSizeLegend: function(x, y) {
		// get label names
		var labels = Array();
		var step = (this.maxSize - this.minSize) / 2;
		for(var i = 0; i < 3; i++) {
			var val = this.minSize + i * step;
			var text = Math.round(val * 100) / 100;
			labels.push(text);
		}

		var label0Width = this.ctx.measureText(labels[0]).width;
		var labelnWidth = this.ctx.measureText(labels[3 - 1]).width;

		var drawX = x + label0Width / 2 + this.gutter;
		var drawY = y + this.gutter / 2;

		// draw label
		this.ctx.textBaseline = "top";
		this.ctx.textAlign = "left";
		this.ctx.font = this.axisLabelSize + "pt " + this.axisLabelFont;
		this.ctx.fillStyle = this.colorDark;
		this.ctx.fillText(this.sizeLabel, drawX, drawY);

		drawY += this.axisLabelSize + this.gutter / 2;

		// draw size circles and values
		this.ctx.font = this.ticsSize + "pt " + this.ticsFont;
		this.ctx.textBaseline = "top";
		this.ctx.textAlign = "center";

		var sizeStep = (this.maxPointSize - this.minPointSize) / 2;
		for(var i = 0; i < 3; i++) {
			var xp = drawX + i * this.sizeBarWidth / 2;
			var radius = this.minPointSize + i * sizeStep;

			var yp = drawY + this.sizeBarHeight/2;
			this.ctx.fillStyle = this.defaultColor;
			this.circle(xp, yp, radius);

			yp = drawY + this.sizeBarHeight + this.gutter / 2;
			this.ctx.fillStyle = this.textColor;
			this.ctx.fillText(labels[i], xp, yp);
		}

		var rightBuf = Math.max(labelnWidth / 2, this.maxPointSize);
		//alert(this.ctx.measureText(label2).width / 2);
		//alert(this.maxPointSize / 2);
		//alert(rightBuf);

		var w = this.sizeBarWidth + label0Width / 2 + rightBuf + this.gutter * 2;
		this.ctx.strokeStyle = this.colorLight;
		//this.crispStrokeRect(x, y, w, h);
		this.crispStrokeRect(x, y, w, this.legendHeight);
	},

	// draw the actual points
	drawPoints: function() {
		//startTime = getCurTime();
		//var plotted = new Array();

		// draw points
		for(var i = 0; i < this.plotX.length; i++) {
			this.ctx.fillStyle = this.plotColor[i];
			// REMOVE
			//this.plotSize[i] *= 1.3;
			this.circle(this.plotX[i], this.plotY[i], this.plotSize[i]);
		}

		//alert(this.plotX[0]);

		// draw highlighted points
		this.ctx.strokeStyle = "rgba(225, 0, 0, 0.8)";
		this.ctx.lineWidth = 1;
		for(var i = 0; i < this.plotX.length; i++) {
			var symbol = this.dataLabel[i].toUpperCase();
			if ( !(symbol in this.highlight) ) {
				continue;
			}

			//if ( symbol in plotted ) {
			//	continue;
			//}

			// only dont plot again if coords are same
			//plotted[symbol] = 1;
			this.circleStroke(this.plotX[i], this.plotY[i], this.plotSize[i] + 2);
		}
		//alert("drew points in " + (getCurTime() - startTime)/1000);
	},

	// save img so that if you select some points, etc., the main
	// image need not be drawn again
	saveImg: function() {
		//startTime = getCurTime();
		this.img.src = this.canvas.toDataURL();
		//alert("saved img in " + (getCurTime() - startTime)/1000);

		this.imgSaved = true;
	},

	// clear the canvas
	clear: function() {
		this.ctx.clearRect(0, 0, this.width, this.height);
		this.imgSaved = false;
	},

	// call to plot the vectors
	plot: function (options) {
		if (! options['x'] || ! options['y']) {
			return;
		}

		this.dataX = options['x'];
		this.dataY = options['y'];

		if (options['color'] && options['color'].length > 0) {
			this.dataColor = options['color'];
			this.customColor = true;
		} else {
			this.dataColor = new Array();
			this.customColor = false;
		}

		if (options['size'] && options['size'].length > 0) {
			this.dataSize = options['size'];
			this.customSize = true;
		} else {
			this.dataSize = new Array();
			this.customSize = false;
		}

		if (options['label']) {
			this.dataLabel = options['label'];
		} else {
			this.dataLabel = new Array();
			for(var i = 1; i < this.dataX.length + 1; i++) {
				var label = 'P' + i;
				this.dataLabel.push(label);
			}
		}

		this.scatter();
	},

	scatter: function() {
		this.clear();
		this.imgSaved = false;
	
		this.getDataRange();
		this.setPaddings();
		this.setTransformation();

		this.drawFrame();
		this.drawPoints();

		this.saveImg();
	}, 

	redraw: function() {
		this.clear();
		this.ctx.drawImage(this.img, 0, 0);
	},

	// draw a rect with crisp boundaries
	crispLine: function(x1, y1, x2, y2) {
		x1 = Math.round(x1);
		x2 = Math.round(x2);
		y1 = Math.round(y1);
		y2 = Math.round(y2);

		// vertical line
		if(x1 == x2) {
			x1 -= 0.5;
			x2 -= 0.5;
		} else if (y1 == y2) {
			y1 -= 0.5;
			y2 -= 0.5;
		}
		//alert(x1 + "," + y1);
		//alert(x2 + "," + y2);


		this.ctx.beginPath();
		this.ctx.moveTo(x1, y1);
		this.ctx.lineTo(x2, y2);
		this.ctx.stroke();
		this.ctx.closePath();

		// can't get crisp line even with this trick
		//this.ctx.beginPath();
		//this.ctx.moveTo(75,  470.5);
		//this.ctx.lineTo(680, 470.5);
		//this.ctx.stroke();
		//this.ctx.closePath();
	},

	// draw a rect with crisp boundaries
	crispFillRect: function(x, y, w, h) {
		x = Math.round(x);
		y = Math.round(y);
		w = Math.round(w);
		h = Math.round(h);

		this.ctx.strokeRect(x, y, w, h);
	},

	// draw a rect with crisp boundaries
	crispStrokeRect: function(x, y, w, h) {
		x = Math.round(x);
		y = Math.round(y);
		w = Math.round(w);
		h = Math.round(h);

		if(this.ctx.lineWidth % 2 == 0) {
		} else {
			x -= 0.5;
			y -= 0.5;
		}

		this.ctx.strokeRect(x, y, w, h);
	},

	// draw a filled circle
	circle: function(x, y, r) {
		this.ctx.beginPath();
		this.ctx.arc(x, y, r, 0, Math.PI*2, true);
		this.ctx.fill();
		this.ctx.closePath();
	},

	// draw a empty circle
	circleStroke: function(x, y, r) {
		this.ctx.beginPath();
		this.ctx.arc(x, y, r, 0, Math.PI*2, true);
		this.ctx.stroke();
		this.ctx.closePath();
	},

	selectAreaRect: function(xp, yp) {
		var x = Math.min(this.selX[0], xp);
		var y = Math.min(this.selY[0], yp);
		var w = Math.abs(xp - this.selX[0]);
		var h = Math.abs(yp - this.selY[0]);

		//alert("(" + x1 + "," + y1 + ") - (" + x2 + "," + y2 + ")");
		//alert(selectionActive);
		//alert("(" + x + "," + y + ") - (" + w + "," + h + ")");

		this.ctx.fillStyle = "rgba(0, 0, 200, 0.20)";
		this.ctx.strokeStyle = "rgba(0, 0, 200, 0.20)";
		this.ctx.lineWidth = 2;

		this.ctx.strokeRect (x, y, w, h);
		this.ctx.fillRect (x, y, w, h);
	}, 

	selectAreaPoly: function (xp, yp) {
		this.ctx.fillStyle = "rgba(0, 0, 200, 0.20)";
		this.ctx.strokeStyle = "rgba(0, 0, 200, 0.20)";
		this.ctx.lineWidth = 2;

		this.ctx.beginPath();
		this.ctx.moveTo(this.selX[0], this.selY[0]);
		for(var i = 1; i < this.selX.length; i++) {
			this.ctx.lineTo(this.selX[i], this.selY[i]);
		}
		this.ctx.lineTo(xp, yp);
		this.ctx.lineTo(this.selX[0], this.selY[0]);

		this.ctx.stroke();
		this.ctx.fill();
		this.ctx.closePath();
	},

	// show selected points
	showSelected: function(selectionType) {
		switch (selectionType) {
			case 'rectangle': 
				var idx = this.getPointsInRect();
				break;
			case 'polygon': 
				var idx = this.getPointsInPoly();
				break;
			default:
				var idx = this.getPointsInRect();
		}

		var col1 = ['Gene'];
		var col2 = [this.xAxisLabel];
		var col3 = [this.yAxisLabel]

		this.ctx.strokeStyle = "rgba(0, 0, 0, 0.6)";
		this.ctx.lineWidth = 1;

		for(var k = 0; k < idx.length; k++){
			var i = idx[k];

			// plot the selected point
			xp = this.plotX[i];
			yp = this.plotY[i];
			this.circleStroke(xp, yp, this.plotSize[i] + 2);

			// save the selected point in an array
			label = this.dataLabel[i];
			xd = this.dataX[i];
			yd = this.dataY[i];

			col1.push(label);
			col2.push(xd);
			col3.push(yd);
		}

		this.processSelectedData(col1, col2, col3);
	},

	// get idx of points inside the selected rectangle
	getPointsInRect: function() {
		var xMin = Math.min(this.selX[0], this.selX[1]);
		var xMax = Math.max(this.selX[0], this.selX[1]);
		var yMin = Math.min(this.selY[0], this.selY[1]);
		var yMax = Math.max(this.selY[0], this.selY[1]);

		var aInRect = new Array();

		// get the points in the rectangle
		for(var i = 0; i < this.plotX.length; i++) {
			if(this.plotX[i] > xMin && this.plotX[i] < xMax 
					&& this.plotY[i] > yMin && this.plotY[i] < yMax) {
				aInRect.push(i);
			}
		}

		return aInRect;
	},

	// get idx of points inside the selected polygon
	getPointsInPoly: function() {
		var selXmin = this.selX.min();
		var selXmax = this.selX.max();
		var selYmin = this.selY.min();
		var selYmax = this.selY.max();

		// disregard points outside the rect bounding the polygon
		var idx = new Array();
		for(var i = 0; i < this.plotX.length; i++) {
			if( this.plotX[i] > selXmin && this.plotX[i] < selXmax && 
					this.plotY[i] > selYmin && this.plotY[i] < selYmax) {
				idx.push(i);
			}
		}


		var aInPoly = new Array();

		//var xp, yp;
		// put the first point again to close the poly
		this.selX.push(this.selX[0]);
		this.selY.push(this.selY[0]);
		for(var k = 0; k < idx.length; k++){
			xp = this.plotX[idx[k]];
			yp = this.plotY[idx[k]];

			// is point in poly
			var inPoly = false;
			var j = this.selX.length - 1;
			for(var i = 0; i < this.selX.length; i++) {
				vertex1x = this.selX[i];
				vertex1y = this.selY[i];
				vertex2x = this.selX[j];
				vertex2y = this.selY[j];

				if (vertex1x < xp && vertex2x >= xp 
						|| vertex2x < xp && vertex1x >= xp)  {
					if (vertex1y + (xp - vertex1x) / (vertex2x - vertex1x) * (vertex2y - vertex1y) < yp) {
						inPoly = !inPoly;
					}
				}

				j = i;
			}

			if(inPoly) aInPoly.push(idx[k]);
		}

		return aInPoly;
	},

	mousedown: function(e) {
		// this is showing undefined, don't know why
		// only undefined problem in attached mouse function
		// alert(this.selectionType);

		viz.redraw();
		var x = e.pageX - e.target.offsetLeft;
		var y = e.pageY - e.target.offsetTop;

		if(viz.selectionType == 'rectangle') {
			viz.selX = [x];
			viz.selY = [y];
			viz.selectionActive = true;
		} else if(viz.selectionType == 'polygon') {
			if(viz.selectionActive == false) {
				viz.selX = [];
				viz.selY = [];
				viz.selectionActive = true;
			} else {
				viz.redraw();
				viz.selectAreaPoly(x, y);
			}
			viz.selX.push(x);
			viz.selY.push(y);
		}
	},

	mousemove: function(e) {
		var x = e.pageX - e.target.offsetLeft;
		var y = e.pageY - e.target.offsetTop;
	
		if(viz.selectionType == 'rectangle') {
			if(viz.selectionActive) {
				viz.redraw();
				viz.selectAreaRect(x, y);
			}
		} else if(viz.selectionType == 'polygon') {
			if(viz.selectionActive) {
				viz.redraw();
				viz.selectAreaPoly(x, y);
			}
		}
	},

	mouseup: function(e) {
		var x = e.pageX - e.target.offsetLeft;
		var y = e.pageY - e.target.offsetTop;

		if(viz.selectionType == 'rectangle') {
			if(viz.selectionActive) {
				viz.selX.push(x);
				viz.selY.push(y);
				viz.showSelected(viz.selectionType);
				//alert("(" + x1 + "," + y1 + ") - (" + x2 + "," + y2 + ")");
			}
			viz.selectionActive = false;
		} else if(viz.selectionType == 'polygon') {
		}
	},

	mouseout: function(e) {
	},

	dblclick: function(e) {
		var x = e.pageX - e.target.offsetLeft;
		var y = e.pageY - e.target.offsetTop;

		if(viz.selectionType == 'rectangle') {
		} else if(viz.selectionType == 'polygon') {
			if(viz.selX.length > 0) {
				viz.selectionActive = false;

				// dbl click causes click to be called twice
				// so data point are already entered twice
				viz.selX.pop();
				viz.selY.pop();
				// clear selection boundary
				viz.redraw();
				viz.showSelected(viz.selectionType);
			}
		}
	},
};
