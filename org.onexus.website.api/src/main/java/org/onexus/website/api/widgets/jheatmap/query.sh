#!/bin/bash

query="DEFINE
	c0='data/gene_topo_vector',
	c1='data/gene_annotations',
	c2='data/topo_annotations',
	c3='data/gene_topo_mutations-combination',
	c4='data/gene_topo_cosmic',
	c5='data/gene_topo_expression-combination',
	c6='data/gene_topo_cna-combination'
ON
	'file:/home/jdeu/workspace/intogen-project'
SELECT
	c1 ('GENEID', 'SYMBOL'),
	c2 ('NAME', 'TOPOID'),
	c3 ('CPVALUE'),
	c4 ('FOUND', 'STUDIED'),
	c5 ('UPREG_PVALUE', 'DOWNREG_PVALUE'),
	c6 ('GAIN_PVALUE', 'LOSS_PVALUE')
FROM
	c0
WHERE
	NOT ( c3.CPVALUE IS NULL )
"

echo $query | curl -X POST -d @- http://localhost:8181/onexus/onx
