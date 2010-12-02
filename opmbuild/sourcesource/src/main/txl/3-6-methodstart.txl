function add_records_to_parameter_list MethodName [id] NumArgs [number]
    replace [list formal_parameter]
        Head [formal_parameter] ', Tail [list formal_parameter]
    construct Remainder [number]
        _ [length Tail]
    construct Index [number]
        NumArgs [- Remainder][-1]
    by
        Head [add_records_to_method_parameter MethodName Index] ', Tail [add_records_to_parameter_list MethodName NumArgs]
end function

rule add_records_to_method_parameter MethodName [id] Index [number]
    replace $ [variable_name]
        ID [id] VariableDeclared [declared_name] Indices [repeat dimension]
    deconstruct VariableDeclared
        Variable [id]
    import AnyStatement [declaration_or_statement]
    construct CauseStatement [declaration_or_statement]
        AnyStatement [create_record_data_statement ID MethodName Variable]
    construct ArgumentStatement [declaration_or_statement]
        AnyStatement [create_argument_received MethodName MethodName Index ID]
    import MethodStart [repeat declaration_or_statement]
    export MethodStart
        MethodStart [. CauseStatement][. ArgumentStatement]
    by
        ID VariableDeclared Indices
end rule

function create_argument_received MethodID [id] InvocationID [id] Index [number] VariableID [id]
    replace [declaration_or_statement]
        Original [declaration_or_statement]
    construct CauseStatement [charlit]
        _ [+ "Recorder._currentRecorder.argumentReceived ("]
          [+ ''"'][+ MethodID][+ ''"'][+ ","]
          [+ ''"'][+ InvocationID][+ ''"'][+ ","]
          [+ Index][+ ","]
          [+ ''"'][+ VariableID][+ ''"'][ + ");"]
    construct DeclarationOrStatement [declaration_or_statement]
        Original [parse CauseStatement]
    by
        DeclarationOrStatement
end function