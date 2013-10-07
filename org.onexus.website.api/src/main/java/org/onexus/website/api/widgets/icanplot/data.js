Array.prototype.max = function(){
	return Math.max.apply( {}, this);
};

Array.prototype.min = function(){
	return Math.min.apply( {}, this);
};

// load, parse and prepare vectors for plotting
data = {
	vals: [],
	ids: [],
	names: [],
	colNames: [],
	success: true,
	filetype: 'none',
	header: false,

	// select the source of data
	// if filetype is not mentioned, infer from extension
	loadData: function (options) {
		if (options['filetype']) {
			this.filetype = options['filetype'];
		} else {
			var parts = options['url'].split('.');
			this.filetype = parts[parts.length - 1];
		}

		if (options['header']) {
			this.header = true;
		}

		if(1) {
			this.loadDataJquery(options['url']);
		} else {
			this.loadRandData();
		}
	},

	loadRandData: function () {
		var numGenes = 1000;
		this.vals = new Array(numGenes);

		for(var i = 0; i < numGenes; i++) {
			var row = new Array();
			row[0] = Math.floor(Math.random() * 400 - 300);	// -300 to +100
			row[1] = Math.floor(Math.random() * 550 - 80);	// -80 to + 500
			row[2] = Math.floor(Math.random() * 100 + 50);	// +50 to +150
			row[3] = Math.floor(Math.random() * 200 - 250);	// -250 to -50
			this.vals[i] = row;
		}

		var numSamples = this.vals[0].length;
		this.colNames = new Array(numSamples);
		for(var i = 0; i < numSamples; i++)
			this.colNames[i] = "Sample " + (i + 1);

		this.names = new Array(numGenes);
		for(var i = 0; i < numGenes; i++)
			this.names[i] = "Gene_" + (i + 1);
	},

	loadDataJava: function (_url) {
		//var startTime = getCurTime();
		var url = new java.net.URL(_url);
		var connect = url.openConnection();
		var input = new java.io.BufferedReader(
		new java.io.InputStreamReader( connect.getInputStream()));
		var line = "";

		line = input.readLine();
		line = input.readLine();
		this.colNames = input.readLine().split('\t').splice(2);
		//alert(this.colNames);

		this.vals = new Array();
		this.names = [];
		while((line = input.readLine()) != null) {
			row = line.split('\t');
			this.names.push(row[1]);
			this.vals.push(row.slice(2));
			//if(this.vals.length % 97 == 0)
			//showProgress(this.vals.length + ' genes loaded');
		}
		//alert(geneNames.length);

		//alert((getCurTime() - startTime) / 1000);
	},

	loadDataJquery: function(_url) {
		//var startTime = getCurTime();

		// to avoid collision with this of ajax
		dthis = this;

		$.ajax({
			url: _url,
			type: 'GET',
			dataType: 'text',
			timeout: 1000,
			async: false,
			cache: false,
			success: function(rdata) {
				dthis.parseData(rdata);
				dthis.success = true;
			},
			error: function(jqXHR, textStatus, errorThrown){
				if (errorThrown == 'URL did not match')
					errorThrown = 'URL not found';

				alert('Error loading data at ' + _url + '.\n' + errorThrown);
				//alert(textStatus);
				//alert(errorThrown);
				dthis.success = false;
			},
		});

		//alert(this.success);

		//alert((getCurTime() - startTime) / 1000);
	},

	parseData: function(dataText) {
		//alert('parsing data');
		//alert(this.filetype);
		switch (this.filetype) {
			case 'TSV': 
			case 'TXT': 
				this.parseTxt(dataText);
				break;
			case 'CSV': 
				this.parseTxt(dataText);
				break;
			case 'GCT': 
				this.parseGct(dataText);
				break;
			case 'PCL': 
				this.parsePcl(dataText);
				break;
			default:
				this.parseTxt(dataText);
		}
	},

	parseTxt: function(dataText) {
		// convert windows based files
		dataText = dataText.replace(/\r/g, '\n');

		var rows = dataText.split('\n');
		//alert(rows.length);

		var delim = '\t';
		var k = 0;

		// get the names of the column from the header, else sample x
		if (this.header) {
			k = 1;
			this.colNames = rows[0].split(delim).splice(2);
		} else {
			this.colNames = new Array();
			for(var j = 2; j < rows[0].split(delim).length; j++) {
				this.colNames.push("Val " + (j - 1));
			}
		}
		//alert(this.colNames);

		this.ids = [];
		this.rowNames = [];
		this.vals = new Array();

		for(; k < rows.length; k++) {
			var cols = rows[k].split(delim);
			if(cols.length < 2) continue;

			this.ids.push(cols[0]);
			this.rowNames.push(cols[1]);

			var data = cols.slice(2);
			this.toFloat(data);
			this.vals.push(data.slice());

			//alert(this.vals);
			//.push(this.toFloat(data));
			//this.vals.push(data);
		}
	},

	parseGct: function(dataText) {
		//var startTime = getCurTime();
		//
		//console.log(dataText);

		var rows = dataText.split('\n');
		//alert(rows.length);
		if(rows.length < 4) {
			alert('Incomplete data');
			return;
		}

		this.vals = new Array();
		this.rowNames = [];
		this.colNames = rows[2].split('\t').splice(2);
		//alert(this.colNames);

		for(var i = 3; i < rows.length; i++) {
			var cols = rows[i].split('\t');
			if(cols.length < 2) continue;

			this.ids.push(cols[0]);
			this.rowNames.push(cols[1]);

			//this.vals.push(this.toFloat(cols.slice(2)));

			var data = cols.slice(2);
			this.toFloat(data);
			this.vals.push(data.slice());

			//if(i % 100 == 0) showProgress( (i - 3) + ' genes loaded');
		}
		//alert(data.length);
	},

	parsePcl: function(dataText) {
	},

	toFloat: function(arr) {
		for(var i = 0; i < arr.length; i++) {
			arr[i] = parseFloat(arr[i]);
		}
	},

	// load required data in 4 single vectors, may directly do this if required
	getCol: function(k) {
		var vals = new Array();
		for(var i = 0; i < this.vals.length; i++) {
			vals.push(this.vals[i][k]);
		}

		return vals;
	},

	// load required data in 4 single vectors, may directly do this if required
	getCols: function(x_axs, y_axs, color, size) {
		var numRows = this.vals.length;
		var dataX = new Array(numRows);
		var dataY = new Array(numRows);
		var dataColor = new Array();
		var dataSize = new Array();

		for(var i = 0; i < numRows; i++) {
			dataX[i] = this.vals[i][x_axs];
			dataY[i] = this.vals[i][y_axs];
			if(color != -1) {
				dataColor.push(this.vals[i][color]);
			}
			if(size != -1) {
				dataSize.push(this.vals[i][size]);
			}
		}

		return {x: dataX, y: dataY, color: dataColor, size: dataSize};
	},

	dummy: "yo"
};

