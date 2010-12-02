% add_records_to_declaration: ADDS RECORDING TO PRE AND POST STATEMENTS FOR A DECLARATION

function add_argument_passes
    replace [any]
        Statement [any]
    by
        Statement [add_argument_passes_to_local_calls][add_argument_passes_to_object_calls]
end function 

rule add_argument_passes_to_local_calls
    replace $ [reference]
        MethodName [id] Component [component]
    deconstruct Component
        MethodArgument [method_argument]
    deconstruct MethodArgument
        '( Arguments [list argument] ')
    construct NumArgs [number]
        _ [length Arguments]
    construct NewMethodArgument [list argument]
        Arguments [add_argument_passes_to_list MethodName NumArgs]
    by
        MethodName Component
end rule

rule add_argument_passes_to_object_calls
    replace $ [reference]
        Object [id] NameComponent [component] ArgumentComponent [component]
    deconstruct NameComponent
        DotID [dot_id]
    deconstruct DotID
        '. MethodName [id]
    deconstruct ArgumentComponent
        MethodArgument [method_argument]
    deconstruct MethodArgument
        '( Arguments [list argument] ')
    construct NumArgs [number]
        _ [length Arguments]
    construct NewMethodArgument [list argument]
        Arguments [add_argument_passes_to_list MethodName NumArgs]
    by
        Object NameComponent ArgumentComponent
end rule

function add_argument_passes_to_list MethodName [id] NumArgs [number]
    replace [list argument]
        Head [argument] ', Tail [list argument]
    construct Remainder [number]
        _ [length Tail]
    construct Index [number]
        NumArgs [- Remainder][-1]
    by
        Head [add_argument_pass MethodName Index] ', Tail [add_argument_passes_to_list MethodName NumArgs]
end function

rule add_argument_pass MethodName [id] Index [number]
    replace $ [reference]
	  VariableID [id] VariableName [id] Dimension [repeat dimension]
    import AnyStatement [declaration_or_statement]
    construct ArgumentPass [declaration_or_statement]
        AnyStatement [create_argument_pass MethodName Index VariableID]
    import PreAdditions [repeat declaration_or_statement]
    export PreAdditions
        PreAdditions [. ArgumentPass]
    by
	  VariableID VariableName Dimension
end rule

function create_argument_pass MethodID [id] Index [number] VariableID [id]
    replace [declaration_or_statement]
        Original [declaration_or_statement]
    construct CauseStatement [charlit]
        _ [+ "Recorder._currentRecorder.argumentPass ("]
          [+ ''"'][+ MethodID][+ ''"'][+ ","]
          [+ Index][+ ","]
          [+ ''"'][+ VariableID][+ ''"'][ + ");"]
    construct DeclarationOrStatement [declaration_or_statement]
        Original [parse CauseStatement]
    by
        DeclarationOrStatement
end function