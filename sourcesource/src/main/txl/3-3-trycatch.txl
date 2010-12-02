function add_records_to_try_block Occurrence [declaration_or_statement] Point [id]
    replace [statement]
        TryStatement [try_statement]
    deconstruct TryStatement
        'try TryBlock [block] CatchClauses [repeat catch_clause] FinallyClause [finally_clause]
    construct CauseStatements [repeat declaration_or_statement]
        _ [. Occurrence]
    construct NewTryBlock [block]
        TryBlock [add_records_to_block CauseStatements]
    construct NewCatchClauses [repeat catch_clause]
        CatchClauses [add_records_to_catch_clauses Point]
    construct NewFinallyClause [finally_clause]
        FinallyClause [add_records_to_finally_clause]
    construct NewTryStatement [try_statement]
        'try NewTryBlock NewCatchClauses NewFinallyClause
    by
        NewTryStatement
end function

function add_records_to_catch_clauses Point [id]
    replace [repeat catch_clause]
        First [catch_clause] Rest [repeat catch_clause]
    by
        First [add_records_to_catch_clause Point] Rest [add_records_to_catch_clauses Point]
end function

function add_records_to_catch_clause Point [id]
    replace [catch_clause]
        'catch '( Modifiers [repeat modifier] Type [type_specifier] VariableName [variable_name] ') Block [block]
    deconstruct VariableName
        Identifier [id] DeclaredName [declared_name]
    deconstruct DeclaredName
        Name [id] _ [opt generic_parameter]
    import AnyStatement [declaration_or_statement]
    construct CauseStatement [declaration_or_statement]
        AnyStatement [create_record_data_statement Identifier Point Name]
    construct CauseStatements [repeat declaration_or_statement]
        _ [. CauseStatement]
    construct NewBlock [block]
        Block [add_records_to_block CauseStatements]
    by
        'catch '( Modifiers Type VariableName ') NewBlock
end function

function add_records_to_finally_clause
    replace [finally_clause]
        'finally Block [block]
    construct EmptyList [repeat declaration_or_statement] _
    construct NewBlock [block]
        Block [add_records_to_block EmptyList]
    by
        'finally NewBlock
end function
