% add_records_for_dependences: PUTS RELATIONSHIPS ON ALL DEPENDENDENT EXPRESSION REFERENCES INTO POST-STATEMENTS

function add_records_for_dependences DeclarationOrStatement [declaration_or_statement] Point [id]
    replace [expression]
        Expression [expression]
    construct References [repeat reference]
        _ [^ Expression]
    construct NewExpression [expression]
        Expression [add_records_for_dependence DeclarationOrStatement Point each References]
    by
        Expression
end function

% add_records_for_dependence: PUTS RELATIONSHIPS ON ONE DEPENDENDENT EXPRESSION REFERENCE INTO POST-STATEMENTS

function add_records_for_dependence DeclarationOrStatement [declaration_or_statement] Point [id] Reference [reference]
    replace [expression]
        Expression [expression]
    deconstruct Reference
	  CauseData [id] CauseName [id] _ [repeat dimension] _ [repeat component]
    construct CauseStatement [declaration_or_statement]
        DeclarationOrStatement [create_used_relationship Point CauseData "Used In Expression"]
    import PostAdditions [repeat declaration_or_statement]
    export PostAdditions
        PostAdditions [. CauseStatement]
    by
        Expression    
end function

function add_records_to_expression_statement Occurrence [declaration_or_statement] Point [id]
    replace [statement]
        ExpressionStatement [expression_statement]
    deconstruct ExpressionStatement
        Expression [expression] ';
    import AnyStatement [declaration_or_statement]
    import PostAdditions [repeat declaration_or_statement]
    export PostAdditions
        PostAdditions [. Occurrence]
    construct NewExpression [expression]
        Expression [add_records_for_dependences AnyStatement Point]
                   [add_effect_of_call_on_object Point]
    by
        ExpressionStatement
end function

rule add_effect_of_call_on_object Point [id]
    replace $ [reference]
        ID [id] Variable [id] Indices [repeat dimension] Components [repeat component]
    deconstruct Components
        Component1 [component] Component2 [component] Rest [repeat component]
    deconstruct Component1
        DotID [dot_id]
    deconstruct Component2
        MethodArgument [method_argument]
    import AnyStatement [declaration_or_statement]
    construct VariableStateStatement [declaration_or_statement]
        AnyStatement [create_record_data_statement ID Point Variable]
    construct GeneratedStatement [declaration_or_statement]
        AnyStatement [create_generated_relationship ID "Affected By Method Call In" Point]
    import PostAdditions [repeat declaration_or_statement]
    export PostAdditions
        PostAdditions [. VariableStateStatement][. GeneratedStatement]
    by
        ID Variable Indices Components
end rule
