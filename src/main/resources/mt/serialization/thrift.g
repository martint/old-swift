grammar thrift;

options {
	language = Java;
}

document:	header* definition*;

header	:	include | cpp_include | namespace;

include	:	'include' literal;

cpp_include
	:	'cpp_include' literal;

namespace
	:	('namespace' ( namespace_scope identifier ) 
		|       ( 'smalltalk.category' smalltalk_identifier ) 
		|       ( 'smalltalk.prefix' identifier ) )
	|       'php_namespace' literal  
	|       'xsd_namespace' literal 
	;
	
	
namespace_scope
	:	 '*' | 'cpp' | 'java' | 'py' | 'perl' | 'rb' | 'cocoa' | 'csharp';

definition
	:	 const | typedef | enum | senum | struct | exception | service
	;

const   :	'const' field_type identifier '=' const_value list_separator?
	;

typedef :	'typedef' definition_type identifier
	;

enum
    :'enum' identifier '{' (identifier ('=' int_constant)? list_separator?)* '}'
    ;

senum
    : 'senum' identifier '{' (literal list_separator?)* '}'
    ;

struct
    : 'struct' identifier 'xsd_all'? '{' field* '}'
    ;

exception
:  'exception' identifier '{' field* '}' 
    ;

service
    :  'service' identifier ( 'extends' identifier )? '{' function* '}'
    ;

field           :  field_id? field_req? identifier ('=' const_value)? xsd_field_options list_separator?
	;
field_id         :  int_constant ':'
	;
field_req        :  'required' | 'optional'
	;	

xsd_field_options :  'xsd_optional'? 'xsd_nillable'? xsd_attrs?
	;
xsd_attrs        :  'xsd_attrs' '{' field* '}'
	;

function        :  'async'? function_type identifier '(' field* ')' throws? list_separator?
	;

function_type    :  field_type | 'void'
	;

throws          :  'throws' '(' field* ')'
	;

field_type       :  identifier | base_type | container_type
	;
definition_type  :  base_type | container_type
	;

base_type        :  'bool' | 'byte' | 'i16' | 'i32' | 'i64' | 'double' |
                            'string' | 'binary' | 'slist'
	;

 container_type   :  map_type | set_type | list_type
	;
map_type         :  'map' cpp_type? '<' field_type ',' field_type '>'
	;
 set_type         :  'set' cpp_type? '<' field_type '>'
	;
list_type        :  'list' '<' field_type '>' cpp_type?
	;
cpp_type         :  'cpp_type' literal
	;


const_value      :  int_constant | double_constant | literal | identifier | const_list | const_map
	;
int_constant     :  ('+' | '-')? digit+
	;
double_constant  :  ('+' | '-')? digit* ('.' digit+)? ( ('E' | 'e') int_constant )?
	;
const_list       : '[' (const_value list_separator?)* ']'
	;
const_map        : '{' (const_value ':' const_value list_separator?)* '}'
	;

literal         :  ('"' ~'"'* '"') | ('\'' ~'\''* '\'')
	;
identifier      :  ( letter | '_' ) ( letter | digit | '.' | '_' )*
	;
smalltalk_identifier    :  ( letter | '_' ) ( letter | digit | '.' | '_' | '-' )*
	;
list_separator   :  ',' | ';'
	;
letter          :  ['A'-'Z'] | ['a'-'z']
	;
digit           :  ['0'-'9']
	;
