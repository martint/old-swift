/**
 *  Copyright 2008 Martin Traverso
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
grammar Thrift;

options {
	language = Java;
	output = AST;
}

tokens {
	INCLUDE;
	TYPEDEF;
	NAMESPACE;
	STRUCT;
	FIELD;
	MAP;
	SET;
	LIST;
	OPTIONAL;
	REQUIRED;
}
    


@parser::header {
       package mt.serialization.parser;
}

@lexer::header {
       package mt.serialization.parser;
}

document: header* definition*;

header:	include | namespace;

include: 'include' LITERAL -> ^(INCLUDE LITERAL)
	;

namespace:	'namespace' namespace_scope IDENTIFIER -> ^(NAMESPACE namespace_scope IDENTIFIER)
	;
	
namespace_scope
	:	 '*' | 'cpp' | 'java' | 'py' | 'perl' | 'rb' | 'cocoa' | 'csharp';

definition
	:	 const_rule | typedef | enum_rule | struct
	;

const_rule   :	'const' field_type IDENTIFIER '=' const_value LIST_SEPARATOR?
	;

typedef :	'typedef' definition_type IDENTIFIER -> ^(TYPEDEF definition_type IDENTIFIER)
	;

enum_rule
    :   'enum' IDENTIFIER '{' (IDENTIFIER ('=' INTEGER)? LIST_SEPARATOR?)* '}'
    ;

struct
    : 'struct' IDENTIFIER '{' field* '}' -> ^(STRUCT field*)
    ;

field:  field_id field_req field_type IDENTIFIER ('=' const_value)? LIST_SEPARATOR?
					-> ^(FIELD field_id field_type IDENTIFIER field_req const_value?)			
	;

field_id         :  INTEGER ':' -> INTEGER
	;

field_req        :   'required'   -> REQUIRED
					| 'optional'? -> OPTIONAL
	;

field_type       :  BASE_TYPE | IDENTIFIER | container_type;

definition_type  :  BASE_TYPE | container_type;

container_type   :  map_type | set_type | list_type
	;
map_type         :  'map' '<' field_type ',' field_type '>' -> ^(MAP field_type field_type)
	;
set_type         :  'set' '<' field_type '>' -> ^(SET field_type)
	;
list_type        :  'list' '<' field_type '>' -> ^(LIST field_type)
	;



const_value      :  INTEGER | DOUBLE | LITERAL | IDENTIFIER | const_list | const_map
	;

INTEGER     :  ('+' | '-')? DIGIT+
	;

DOUBLE  :  ('+' | '-')? DIGIT* ('.' DIGIT+)? ( ('E' | 'e') INTEGER )?
	;

const_list       : '[' (const_value LIST_SEPARATOR?)* ']'
	;
const_map        : '{' (const_value ':' const_value LIST_SEPARATOR?)* '}'
	;


LITERAL         :  ('"' ~'"'* '"') | ('\'' ~'\''* '\'')
	;

BASE_TYPE        :  'bool' | 'byte' | 'i16' | 'i32' | 'i64' | 'double' |
                    'string' | 'binary'
	;

IDENTIFIER      :  ( LETTER | '_' ) ( LETTER | DIGIT | '.' | '_' )*
	;


LIST_SEPARATOR   :  ',' | ';'
	;

fragment
LETTER          :  'A'..'Z' | 'a'..'z';


fragment
DIGIT           :  '0'..'9';

WS	:	(' ' | '\t' | '\n')+ { $channel = HIDDEN; }
    ;

COMMENT
    :   '/*' (options {greedy=false;} : .)* '*/' { $channel = HIDDEN; }
    |   ('//' | '#') (~'\n')* { $channel = HIDDEN; }
    ;
