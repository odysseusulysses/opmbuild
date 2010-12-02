include "1-explicate.txl"

function identify
    replace [program]
        P [program]
    export DeclaredVariables [repeat id]
        _
    by
        P [name_statements_in_class]
          [name_variables_in_class]    % Give unique identifiers to all variables
end function

%%% Give unique identifiers to all statements and declarations

rule name_statements_in_class
    replace [class_declaration]
        Header [class_header] Body [class_body]
    deconstruct Header
        _ [repeat modifier] class ClassName [class_name] _ [opt extends_clause] _ [opt implements_clause]
    deconstruct ClassName
        DeclaredName [declared_name]
    deconstruct DeclaredName
        Name [id] _ [opt generic_parameter]
    construct RevisedBody [class_body]
        Body [name_statements_in_method Name]
    where not
        Body [= RevisedBody]
    by
        Header RevisedBody
end rule

rule name_statements_in_method UpperPrefix [id]
    replace $ [method_declaration]
        Access [repeat modifier] Generic [opt generic_parameter] Return [type_specifier] Header [method_declarator] Exceptions [opt throws] Body [method_body]
    deconstruct Header
        MethodName [method_name] '( _ [list formal_parameter] ') _ [repeat dimension]
    deconstruct MethodName
        DeclaredName [declared_name]
    deconstruct DeclaredName
        Name [id] _ [opt generic_parameter]
    construct Prefix [id]
        UpperPrefix [_ Name]
    by
        Access Generic Return Header Exceptions Body [name_statements Prefix][name_declarations Prefix]
end rule

rule name_statements Prefix [id]
    replace [declaration_or_statement]
        Statement [statement]
    construct ID [id]
        Prefix [+ "_Statement"][!]
    by
        ID Statement
end rule

rule name_declarations Prefix [id]
    replace [declaration_or_statement]
        LocalVariableDeclaration [local_variable_declaration]
    construct ID [id]
        Prefix [+ "_Declaration"][!]
    import DeclaredVariables [repeat id]
    export DeclaredVariables 
        DeclaredVariables [. ID]
    by
        ID LocalVariableDeclaration
end rule

%%% Give unique identifiers to all variables

rule name_variables Prefix [id]
    replace [variable_name]
        VariableDeclared [declared_name] Indices [repeat dimension]
    deconstruct VariableDeclared
        Variable [id]
    construct ID [id]
        Prefix [_ Variable]
    import DeclaredVariables [repeat id]
    export DeclaredVariables 
        DeclaredVariables [. ID]
    by
        ID VariableDeclared Indices
end rule

rule name_expression_references Prefix [id]
    replace [primary]
        Reference [reference]
    construct RevisedReference [reference]
        Reference [name_reference Prefix]
    where not
        RevisedReference [= Reference]
    by
        RevisedReference
end rule

function name_reference Prefix [id]
    replace [reference]
        Variable [id] Indices [repeat dimension] Components [repeat component]
    import DeclaredVariables [repeat id]
    construct ID [id]
        Prefix [_ Variable]
    deconstruct * [id] DeclaredVariables
        ID
    by
        ID Variable Indices Components
end function

rule name_variables_in_class
    replace [class_declaration]
        Header [class_header] Body [class_body]
    deconstruct Header
        _ [repeat modifier] class ClassName [class_name] _ [opt extends_clause] _ [opt implements_clause]
    deconstruct ClassName
        DeclaredName [declared_name]
    deconstruct DeclaredName
        Name [id] _ [opt generic_parameter]
    construct RevisedBody [class_body]
        Body [name_variables_in_method Name]
    where not
        Body [= RevisedBody]
    by
        Header RevisedBody
end rule

rule name_variables_in_method UpperPrefix [id]
    replace $ [method_declaration]
        Access [repeat modifier] Generic [opt generic_parameter] Return [type_specifier] Header [method_declarator] Exceptions [opt throws] Body [method_body]
    deconstruct Header
        MethodName [method_name] '( Parameters [list formal_parameter] ') Dimensions [repeat dimension]
    deconstruct MethodName
        DeclaredName [declared_name]
    deconstruct DeclaredName
        Name [id] _ [opt generic_parameter]
    construct Prefix [id]
        UpperPrefix [_ Name]
    construct NewHeader [method_declarator]
        MethodName '( Parameters [name_variables Prefix] ') Dimensions
    by
        Access Generic Return NewHeader Exceptions Body [name_variables Prefix][name_expression_references Prefix]
end rule
