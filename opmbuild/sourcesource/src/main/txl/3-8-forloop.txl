function add_records_to_for_statement Occurrence [declaration_or_statement] Point [id] %% Add a declarartion statement infront of the loop execution to record any new variables declared
    replace [statement]
	ForStatement [for_statement]
    deconstruct ForStatement
	'for '( ForInit [for_init] ForExpression [for_expression] ForUpdate [for_update] ') Statement [statement]
    deconstruct Statement
	Block [block]
    deconstruct ForInit
	LocalVarDec [local_variable_declaration]
    deconstruct LocalVarDec
	VarDec [variable_declaration]
    export VarDec
    construct StringExpress [id]
	Point [reparse Express]
    construct NewStatement [charlit]
        _ [+ "Recorder._currentRecorder.used ("][+ ''"'][+ Point][+ ''"'][+ ", "][+ StringExpress][+ ", "][+ ''"'][+ "Execution Cause By"][+ ''"'][+ ");"]
    construct CauseStatement [repeat declaration_or_statement]
        Occurrence [parse NewStatement]
    construct NewSubStatement [statement]
	Block [add_records_to_block CauseStatement] %% Same problem as switch
    by
	'for '( '; ForExpression ForUpdate ') NewSubStatement
end function

%%function move_declaration
%%    
%%end function
