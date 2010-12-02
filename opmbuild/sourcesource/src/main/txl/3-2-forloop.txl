function add_records_to_for_in_statement Occurrence [declaration_or_statement] Point [id]
    replace [statement]
        ForInStatement [for_in_statement]
    deconstruct ForInStatement
        'for '( ForInInit [for_in_init] : Expression [expression] ') Substatement [statement]
    deconstruct ForInInit
        _ [repeat modifier] _ [type_specifier] VariableName [variable_name]
    deconstruct VariableName
        Identifier [id] DeclaredName [declared_name] Dimensions [repeat dimension]
    deconstruct DeclaredName
        Name [id] _ [opt generic_parameter]
    construct CauseStatement [declaration_or_statement]
        Occurrence [create_record_data_statement Identifier Point Name]
    construct CauseStatements [repeat declaration_or_statement]
        _ [. Occurrence][. CauseStatement]
    deconstruct Substatement
        Block [block]
    construct NewSubstatement [statement]
        Block [add_records_to_block CauseStatements]
    construct NewForInStatement [for_in_statement]
        'for '( ForInInit : Expression ') NewSubstatement
    by
        NewForInStatement
end function
