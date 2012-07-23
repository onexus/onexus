grammar Oql;

options {
  backtrack=true;
}

@lexer::header {
  package org.onexus.collection.query.parser.internal;

  import org.onexus.collection.api.query.*;
}

@parser::header {
  package org.onexus.collection.query.parser.internal;

  import org.onexus.collection.api.query.*;
}

@members {
  private Query query = new Query();

  public Query getQuery() {
        return query;
  }
}

oql 
	: 	defineClause (onClause)? selectClause fromClause (whereClause)? (orderbyClause)? (limitClause)?
;

/* DEFINE clause */
defineClause 
	:	DEFINE defineItem ( ',' defineItem )*
;

defineItem 
	:	alias=varname '=' uri=string
		{ query.addDefine( $alias.text, $uri.v ); }
;

/* ON clause */
onClause 
	:	ON uri=string
		{ query.setOn($uri.v); }
;

/* SELECT clause */
selectClause 
	:	SELECT selectItem ( ',' selectItem )*
;

selectItem 
	:	alias=varname ('(' fields=selectFields ')')
		{ query.addSelect($alias.text, $fields.l); }
;

selectFields returns [List<String> l = new ArrayList<String>();] 
	:
		first=string { $l.add($first.v); }
	    ( ',' other=string { $l.add($other.v); })*
	;

/* FROM clause */
fromClause :
    FROM alias=varname { query.setFrom($alias.text); }
;

/* WHERE clause */
whereClause :
    WHERE filter=filterClause { query.setWhere($filter.f); }
;

filterClause returns [Filter f]
	:	left=filterItem {$f=$left.f;} 
		(
		(AND and=filterItem {$f=new And($left.f, $and.f);})
		|
		(OR or=filterItem {$f=new Or($left.f, $or.f);})
		)?
;

filterItem returns [Filter f]
	:	item=filterAtomic {$f=$item.f;} 
		| LPAREN item=filterClause RPAREN {$f=$item.f;}
;

filterAtomic returns [Filter f]
	: 	equal=filterEqual {$f=$equal.f;}
	     | equalid=filterEqualId {$f=$equalid.f;}
		 | notequal=filterNotEqual {$f=$notequal.f;}
		 | lt=filterLessThan {$f=$lt.f;}
		 | lte=filterLessThanOrEqual {$f=$lte.f;}
		 | gt=filterGreaterThan {$f=$gt.f;}
		 | gte=filterGreaterThanOrEqual {$f=$gte.f;}
		 | contains=filterContains {$f=$contains.f;}
		 | isnull=filterIsNull {$f=$isnull.f;}
		 | in=filterIn {$f=$in.f;}
		 | not=filterNot {$f=$not.f;}
;

filterNot returns [Not f]
    :
        'NOT' ( f1=filterAtomic { $f = new Not( $f1.f ); } | LPAREN f2=filterClause { $f = new Not( $f2.f ); } RPAREN )
    ;

filterEqual returns [Equal f]
	:	alias=varname '.' field=varname '=' value=numberOrStringOrDateOrTime    { $f=new Equal($alias.text, $field.text, $value.v); }
;

filterEqualId returns [EqualId f]
    :   alias=varname '=' value=numberOrStringOrDateOrTime  { $f=new EqualId($alias.text, $value.v); }
;

filterNotEqual returns [NotEqual f]
	:	alias=varname '.' field=varname ('!='|'<>') value=numberOrStringOrDateOrTime  { $f=new NotEqual($alias.text, $field.text, $value.v); }
;

filterContains returns [Contains f]
	:	alias=varname '.' field=varname 'CONTAINS' value=string { $f=new Contains($alias.text, $field.text, $value.v); }
;


filterLessThan returns [LessThan f]
	:	alias=varname '.' field=varname '<' value=numberOrDateOrTime { $f=new LessThan($alias.text, $field.text, $value.v); }
;

filterLessThanOrEqual returns [LessThanOrEqual f]
	:	alias=varname '.' field=varname '<=' value=numberOrDateOrTime { $f=new LessThanOrEqual($alias.text, $field.text, $value.v); }
;

filterGreaterThan returns [GreaterThan f]
	:	alias=varname '.' field=varname '>' value=numberOrDateOrTime { $f=new GreaterThan($alias.text, $field.text, $value.v); }
;

filterGreaterThanOrEqual returns [GreaterThanOrEqual f]
	:	alias=varname '.' field=varname '>=' value=numberOrDateOrTime { $f=new GreaterThanOrEqual($alias.text, $field.text, $value.v); }
;

filterIsNull returns [IsNull f]
	:	alias=varname '.' field=varname 'IS NULL' { $f=new IsNull($alias.text, $field.text); }
;

filterIn returns [In f]
    :   alias=varname '.' field=varname { $f=new In($alias.text, $field.text); }
        'IN' LPAREN first=numberOrStringOrDateOrTime { $f.addValue($first.v); } (',' other=numberOrStringOrDateOrTime { $f.addValue($other.v); } )* RPAREN
    ;


/* ORDER BY clause */
orderbyClause :
    'ORDER BY' first=orderItem { query.addOrderBy($first.o); }
    ( ',' other=orderItem { query.addOrderBy($other.o); } )*
;

orderItem returns [ OrderBy o ] :
    collectionRef=varname '.' fieldId=varname { $o = new OrderBy($collectionRef.text, $fieldId.text); }
    (ascDesc=('ASC' | 'DESC') {  $o.setAscendent($ascDesc.text.equalsIgnoreCase("ASC")); } ) ?
;

/* LIMIT clause */
limitClause :
    'LIMIT' offset=integer ',' count=integer { query.setOffset(Long.valueOf($offset.text)); query.setCount(Long.valueOf($count.text)); }
;

/* Common */

numberOrStringOrDateOrTime returns [Object v]
    :
    ( ndt=numberOrDateOrTime    { $v = $ndt.v; }
    | str=string                { $v = $str.v; }
    )
;

numberOrDateOrTime returns [Object v]
    :
    ( num=number    { $v = $num.v; }
    | dte=t_date      { $v = $dte.v; }
    | tme=t_time      { $v = $tme.v; }
    | tst=t_timestamp { $v = $tst.v; }
    )
;



integer returns [Long v]:
	value=(DIGIT)+ { $v = Filter.convertToLong($value.text); }
	;

number returns [Double v]:
    	value=doubleAndInteger { $v = Filter.convertToDouble($value.text); }
;

doubleAndInteger :	
	(DIGIT)+ ( '.' (DIGIT)* (EXPONENT)? | EXPONENT)?
;

t_timestamp returns [java.sql.Timestamp v]:
    '#' value= '#' { $v = java.sql.Timestamp.valueOf($value.text); }
;

t_time returns [java.sql.Time v]:
    '#' value=time '#' { $v = java.sql.Time.valueOf($value.text); }
;

t_date returns [java.sql.Date v]:
    '#' value=date '#' { $v = java.sql.Date.valueOf($value.text); }
;

string returns [String v]:
	value=STRING { $v = Query.unescapeString($value.text); }
;

timestamp :
        date time ( '.' DIGIT+ )?
;

date :
    	DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT
;

time :
        DIGIT DIGIT ':' DIGIT DIGIT ':' DIGIT DIGIT
;


/* Literals */

varname :
     	LETTER (LETTER | DIGIT | MARK)*
;
	
STRING :
    	'\'' (~'\'' | '\\\'')* '\''
;

fragment EXPONENT
:
    	'e' ( '+' | '-' )? (DIGIT)+
;

WS :
    	(' ' | '\t' | '\n' | '\r')+  { $channel=HIDDEN;}
;

LETTER :
    	'a'..'z' | 'A'..'Z'
;

DIGIT :
    	'0'..'9'
;

MARK:
    '-' | '_' | '!' | '~' | '*'
;

HEX:
    	'%' (DIGIT | HEX_LETTER) (DIGIT | HEX_LETTER)
;

fragment HEX_LETTER:
    	'a'..'f' | 'A'..'F'
;

LPAREN : '(' ;

RPAREN : ')' ;

OR : 'OR' | 'or';

DEFINE
	:	'DEFINE' | 'define'
	;

ON
	:	'ON' | 'on'
	;

SELECT
	:	'SELECT' | 'select'
	;

FROM
	:	'FROM' | 'from'
	;

WHERE
	:	'WHERE' | 'where'
	;

AND
	:	'AND' | 'and'
	;