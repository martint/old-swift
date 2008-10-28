grammar Thrift;

options {
	language = Java;
}

@parser::header {
       package mt.serialization.parser;
}

document: header* definition*;

header:	include | namespace;

include: 'include' literal
	;

namespace:	'namespace' namespace_scope identifier
	;
	


namespace_scope
	:	 '*' | 'cpp' | 'java' | 'py' | 'perl' | 'rb' | 'cocoa' | 'csharp';

definition
	:	 const | typedef | enum_rule | struct
	;

const   :	'const' field_type identifier '=' const_value LIST_SEPARATOR?
	;

typedef :	'typedef' definition_type identifier
	;

enum_rule
    :'enum' identifier '{' (identifier ('=' INTEGER)? LIST_SEPARATOR?)* '}'
    ;

struct
    : 'struct' identifier '{' field* '}'
    ;

field           :  field_id? field_req? identifier ('=' const_value)? LIST_SEPARATOR?
	;

field_id         :  INTEGER ':'
	;

field_req        :  'required' | 'optional'
	;

field_type       :  identifier | base_type | container_type
	;
definition_type  :  base_type | container_type
	;

base_type        :  'bool' | 'byte' | 'i16' | 'i32' | 'i64' | 'double' |
                    'string' | 'binary' 
	;

container_type   :  map_type | set_type | list_type
	;
map_type         :  'map' '<' field_type ',' field_type '>'
	;
set_type         :  'set' '<' field_type '>'
	;
list_type        :  'list' '<' field_type '>'
	;



const_value      :  INTEGER | DOUBLE | literal | identifier | const_list | const_map
	;

INTEGER     :  ('+' | '-')? DIGIT+
	;

DOUBLE  :  ('+' | '-')? DIGIT* ('.' DIGIT+)? ( ('E' | 'e') INTEGER )?
	;

const_list       : '[' (const_value LIST_SEPARATOR?)* ']'
	;
const_map        : '{' (const_value ':' const_value LIST_SEPARATOR?)* '}'
	;


literal         :  ('"' ~'"'* '"') | ('\'' ~'\''* '\'')
	;

identifier      :  ( LETTER | '_' ) ( LETTER | DIGIT | '.' | '_' )*
	;


LIST_SEPARATOR   :  ',' | ';'
	;

fragment
LETTER          :  'A'..'Z' | 'a'..'z';


fragment
DIGIT           :  '0'..'9';

WS	:	(' ' | '\t' | '\n')+ { channel = HIDDEN; }
    ;
