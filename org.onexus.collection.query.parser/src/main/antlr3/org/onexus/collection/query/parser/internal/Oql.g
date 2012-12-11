grammar Oql;

options {
  backtrack=true;
  memoize=true;
}

@lexer::header {
  package org.onexus.collection.query.parser.internal;

  import org.onexus.collection.api.query.*;
  import org.onexus.resource.api.ORI;
}

@parser::header {
  package org.onexus.collection.query.parser.internal;

  import org.onexus.collection.api.query.*;
  import org.onexus.resource.api.ORI;
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
	:	'DEFINE' defineItem ( ',' defineItem )*
;

defineItem 
	:	alias=varname '=' ori=string
		{ query.addDefine( $alias.text, new ORI($ori.v) ); }
;

/* ON clause */
onClause 
	:	'ON' ori=string
		{ query.setOn( new ORI($ori.v)); }
;

/* SELECT clause */
selectClause 
	:	'SELECT' selectItem ( ',' selectItem )*
;

selectItem 
	:	alias=varname ('(' fields=selectFields ')')
		{ query.addSelect($alias.text, $fields.l); }
;

selectFields returns [List<String> l = new ArrayList<String>();] 
	:
		first=varname { $l.add($first.v); }
	    ( ',' other=varname { $l.add($other.v); })*
	;

/* FROM clause */
fromClause :
    'FROM' alias=varname { query.setFrom($alias.text); }
;

/* WHERE clause */
whereClause :
    'WHERE' filter=filterClause { query.setWhere($filter.f); }
;

filterClause returns [Filter f]
	:	left=filterItem {$f=$left.f;} 
		(
		('AND' and=filterItem {$f=new And($left.f, $and.f);})
		|
		('OR' or=filterItem {$f=new Or($left.f, $or.f);})
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
	:	alias=varname '.' field=varname '=' value=string    { $f=new Equal($alias.text, $field.text, $value.v); }
;

filterEqualId returns [EqualId f]
    :   alias=varname '=' value=string  { $f=new EqualId($alias.text, $value.v); }
;

filterNotEqual returns [NotEqual f]
	:	alias=varname '.' field=varname ('!='|'<>') value=string  { $f=new NotEqual($alias.text, $field.text, $value.v); }
;

filterContains returns [Contains f]
	:	alias=varname '.' field=varname 'CONTAINS' value=string { $f=new Contains($alias.text, $field.text, $value.v); }
;


filterLessThan returns [LessThan f]
	:	alias=varname '.' field=varname '<' value=string { $f=new LessThan($alias.text, $field.text, $value.v); }
;

filterLessThanOrEqual returns [LessThanOrEqual f]
	:	alias=varname '.' field=varname '<=' value=string { $f=new LessThanOrEqual($alias.text, $field.text, $value.v); }
;

filterGreaterThan returns [GreaterThan f]
	:	alias=varname '.' field=varname '>' value=string { $f=new GreaterThan($alias.text, $field.text, $value.v); }
;

filterGreaterThanOrEqual returns [GreaterThanOrEqual f]
	:	alias=varname '.' field=varname '>=' value=string { $f=new GreaterThanOrEqual($alias.text, $field.text, $value.v); }
;

filterIsNull returns [IsNull f]
	:	alias=varname '.' field=varname 'IS NULL' { $f=new IsNull($alias.text, $field.text); }
;

filterIn returns [In f]
    :   alias=varname '.' field=varname { $f=new In($alias.text, $field.text); }
        'IN' LPAREN first=string { $f.addValue($first.v); } (',' other=string { $f.addValue($other.v); } )* RPAREN
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
    'LIMIT' offset=string ',' count=string { query.setOffset(Long.valueOf($offset.v)); query.setCount(Long.valueOf($count.v)); }
;

/* Common */
string returns [String v]:
	value=STRING { $v = Query.unescapeString($value.text); }
;

/* Literals */

varname returns [String v]:
	value=IDENTIFIER { $v = $value.text; }
	;

IDENTIFIER 
    : 
        (LETTER | DIGIT | '_' | '-' )*
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

fragment LETTER :
    	 'a'..'z' | 'A'..'Z' 
;


fragment DIGIT :
    	'0'..'9'
;



MARK:
    '-' | '_' | '!' | '~' | '*'
;


LPAREN : '(' ;

RPAREN : ')' ;