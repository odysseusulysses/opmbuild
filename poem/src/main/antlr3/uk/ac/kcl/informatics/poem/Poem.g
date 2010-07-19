grammar Poem;

options {
  output = AST;
}

tokens {
 AGENT      = 'agent#';
 ANNOTATION = 'annotation#';
 ARTIFACT   = 'artifact#';
 INOUT      = 'inOut#';
 INPUTS     = 'inputs#';
 LABEL      = 'label#';
 NAME       = 'name#';
 OUTPUTS    = 'outputs#';
 POEM       = 'poem#';
 PROCESS    = 'process#';
 REFERENCE  = 'reference#';
}

@header {package uk.ac.kcl.informatics.opmbuild.format.poem;}
@lexer::header {package uk.ac.kcl.informatics.opmbuild.format.poem;}

agent         : '<' (label | name)? '>' reference? annotation*
             -> ^(AGENT label? name? reference? annotation*);
annotation    : '+' ID '=' STRING
             -> ^(ANNOTATION ID STRING);
artifact      : '(' ID (label | name)? ')' reference? annotation*
             -> ^(ARTIFACT ID label? name? reference? annotation*);
inOut         : inputs process outputs '.'
             -> ^(INOUT inputs process outputs);
inputs        : (agent | artifact)*
             -> ^(INPUTS agent* artifact*);
label         : (ID | STRING)
             -> ^(LABEL ID? STRING?);
name          : '*' ID
             -> ^(NAME ID);
outputs       : artifact*
             -> ^(OUTPUTS artifact*);
poem          : inOut* EOF
             -> ^(POEM inOut*);
process       : '[' label? ']' annotation*
             -> ^(PROCESS label? annotation*);
reference     : '*' ID
             -> ^(REFERENCE ID);

WHITESPACE : ( '\t' | ' ' | '\r' | '\n'| '\u000C' )+ { $channel = HIDDEN; };
ID         : ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|':')+ ;
STRING     : '"' (ESC_CHAR | ~('"' | '\\'))* '"' ;
ESC_CHAR   : '\\' ('"' | '\\') ;
