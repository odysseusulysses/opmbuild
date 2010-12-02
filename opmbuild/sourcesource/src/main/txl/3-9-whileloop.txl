function add_records_to_while_statement Occurrence [declaration_or_statement] Point [id]
    replace [statement]
	WhileStatement [while_statement]
    deconstruct WhileStatement
	'while '( Express [expression] ') Statement [statement]
%%    deconstruct Express
%%	AssignmentExpress [assignment_expression]
%%    deconstruct AssignmentExpress
%%	Unary [unary_expression]  AssignOp [assignment_operator] AssignExp [assignment_expression]
    deconstruct Statement
	Block [block]
%%    construct StringExpress [id]
%%	Point [reparse Express]
    construct CauseStatement [repeat declaration_or_statement]
	_ [. Occurrence]
    construct NewSubStatement [statement]
	Block [add_records_to_block CauseStatement]
    by
	'while '( Express ') NewSubStatement
end function
