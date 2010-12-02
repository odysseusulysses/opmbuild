include "2-identify.txl"

%%% Necessary main transformation function, calls all other rules

function record
    replace [program]
        P [program]
    export PreAdditions [repeat declaration_or_statement]
        _
    export PostAdditions [repeat declaration_or_statement]
        _
    by
        P [find_a_statement]
          [add_record_import]
          [add_records_to_program]     % Add causal relationship recording statements to all methods
end function

rule find_a_statement
    replace $ [declaration_or_statement] AnyStatement [declaration_or_statement]
    export AnyStatement
    by AnyStatement
end rule

%%% Import recording classes

rule add_record_import
    replace $ [package_declaration]
        Header [opt package_header] Imports [repeat import_declaration] Types [repeat type_declaration]
    deconstruct Imports
        First [import_declaration] Rest [repeat import_declaration]
    construct ImportString [stringlit]
        _ [+ "import org.sourcesource.*;"]
    construct ImportStatement [import_declaration]
        First [parse ImportString]
    by
        Header Imports [. ImportStatement] Types
end rule

%%% Generic rules to add cause and effect recording

function create_record_process_statement Class [id]
    replace [declaration_or_statement]
        Original [declaration_or_statement]
    construct CauseStatement [charlit]
        _ [+ "Recorder._currentRecorder.processPoint ("]
          [+ ''"'][+ Class][+ ''"'][ + ");"]
    construct DeclarationOrStatement [declaration_or_statement]
        Original [parse CauseStatement]
    by
        DeclarationOrStatement
end function

function create_record_data_statement Persistent [id] Class [id] Value [id]
    replace [declaration_or_statement]
        Original [declaration_or_statement]
    construct CauseStatement [charlit]
        _ [+ "Recorder._currentRecorder.persistentPart ("][+ ''"'][+ Persistent][+ ''"'][+ ", "]
          [+ ''"'][+ Class][+ ''"'][+ ", "][ + Value][ + ");"]
    construct DeclarationOrStatement [declaration_or_statement]
        Original [parse CauseStatement]
    by
        DeclarationOrStatement
end function

function create_used_relationship EffectOccurrence [id] CausePersistent [id] CauseRole [stringlit]
    replace [declaration_or_statement]
        Original [declaration_or_statement]
    construct CauseStatement [charlit]
        _ [+ "Recorder._currentRecorder.used ("][+ ''"'][+ EffectOccurrence][+ ''"'][+ ", "][+ ''"'][+ CausePersistent][+ ''"'][+ ", "][+ ''"'][+ CauseRole][+ ''"'][+ ");"]
    construct DeclarationOrStatement [declaration_or_statement]
        Original [parse CauseStatement]
    by
        DeclarationOrStatement
end function

function create_generated_relationship EffectPersistent [id] EffectRole [stringlit] CauseClass [id]
    replace [declaration_or_statement]
        Original [declaration_or_statement]
    construct CauseStatement [charlit]
        _ [+ "Recorder._currentRecorder.wasGeneratedBy ("][+ ''"'][+ EffectPersistent][+ ''"'][+ ", "][+ ''"'][+ EffectRole][+ ''"'][+ ", "][+ ''"'][+ CauseClass][+ ''"'][+ ");"]
    construct DeclarationOrStatement [declaration_or_statement]
        Original [parse CauseStatement]
    by
        DeclarationOrStatement
end function

function create_push_account
    replace [declaration_or_statement]
        Original [declaration_or_statement]
    construct CauseStatement [charlit]
        _ [+ "Recorder._currentRecorder.pushAccount ();"]
    construct DeclarationOrStatement [declaration_or_statement]
        Original [parse CauseStatement]
    by
        DeclarationOrStatement
end function

function create_pop_account
    replace [declaration_or_statement]
        Original [declaration_or_statement]
    construct CauseStatement [charlit]
        _ [+ "Recorder._currentRecorder.popAccount ();"]
    construct DeclarationOrStatement [declaration_or_statement]
        Original [parse CauseStatement]
    by
        DeclarationOrStatement
end function

%% Specific rules to add occurrences and relationships at program points

% add_records_to_program: ADDS RECORDING THROUGHOUT PROGRAM

rule add_records_to_program
    replace $ [method_declaration]
        Access [repeat modifier] Generic [opt generic_parameter] Return [type_specifier] Head [method_declarator] Throws [opt throws] Body [method_body]
    deconstruct Head
        MethodName [method_name] '( Parameters [list formal_parameter] ') _ [repeat dimension]
    deconstruct MethodName
        DeclaredName [declared_name]
    deconstruct DeclaredName
        Name [id]
    deconstruct Body
        Block [block]
    import AnyStatement [declaration_or_statement]
    construct CauseStatement [declaration_or_statement]
        AnyStatement [create_record_process_statement Name]
    export MethodStart [repeat declaration_or_statement]
        _ [. CauseStatement]
    construct NumArgs [number]
        _ [length Parameters]
    construct NewParameters [list formal_parameter]
        Parameters [add_records_to_parameter_list Name NumArgs]
    import MethodStart
    construct NewBody [method_body]
        Block [add_records_to_block MethodStart]
    by 
        Access Generic Return Head Throws NewBody
end rule

% add_records_to_block: ADDS RECORDING TO CONTENTS OF A BLOCK, WITH OPTIONAL STATEMENTS INSERTED AT START

function add_records_to_block InsertAtStart [repeat declaration_or_statement]
    replace [block]
        '{ Statements [repeat declaration_or_statement] '}
    import PreAdditions [repeat declaration_or_statement] 
    import PostAdditions [repeat declaration_or_statement]
    export SavedPreAdditions [repeat declaration_or_statement] PreAdditions
    export SavedPostAdditions [repeat declaration_or_statement] PostAdditions
    construct NewStatements [repeat declaration_or_statement]
        Statements [add_records_to_declaration_or_statement_list]
    export PreAdditions SavedPreAdditions
    export PostAdditions SavedPostAdditions
    by
        '{ InsertAtStart [. NewStatements] '}
end function

% add_records_to_declaration_or_statement_list: ADDS RECORDING TO EACH DECLARATION/STATEMENT IN A REPEAT
%    BOTH INSERTS RECORDING STATEMENTS BEFORE AND AFTER DECLARATION/STATEMENT
%    AND REPLACES STATEMENT WITH ONE POTENTIALLY CONTAINING FURTHER RECORDING

function add_records_to_declaration_or_statement_list
    replace [repeat declaration_or_statement]
        Head [declaration_or_statement] Tail [repeat declaration_or_statement]
    export PreAdditions [repeat declaration_or_statement] _
    export PostAdditions [repeat declaration_or_statement] _
    construct RevisedHead [declaration_or_statement]
        Head [add_records_to_declaration][add_records_to_statement]
    import PreAdditions
    import PostAdditions
    construct RevisedTail [repeat declaration_or_statement]
        Tail [add_records_to_declaration_or_statement_list]
    construct RevisedList [repeat declaration_or_statement]
        PreAdditions [. RevisedHead][. PostAdditions][. RevisedTail]
    by
        RevisedList
end function

% add_records_to_statement: ADDS RECORDING TO STATEMENT (BEFORE, AFTER AND WITHIN AS APPROPRIATE)

function add_records_to_statement
    replace [declaration_or_statement]
        Point [id] Statement [statement]
    import AnyStatement [declaration_or_statement]
    construct Occurrence [declaration_or_statement]
        AnyStatement [create_record_process_statement Point]
    by
        Point Statement [add_records_to_for_in_statement Occurrence Point]
                        [add_records_to_try_block Occurrence Point]
                        [add_records_to_expression_statement Occurrence Point]
                        [add_argument_passes]
end function

include "3-1-declaration.txl"
include "3-2-forloop.txl"
include "3-3-trycatch.txl"
include "3-4-expression.txl"
include "3-5-invocation.txl"
include "3-6-methodstart.txl"
