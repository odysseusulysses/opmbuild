include "java_with_occurrences.grm"

function explicate
    replace [program]
        P [program]
    by
        P [if_substatement_ensure_else]
          [if_substatement_then_block]
          [if_substatement_else_block]
end function

%%% Ensure all sub-statements are in blocks

rule if_substatement_ensure_else
    replace [if_statement]
        'if '( Condition [expression] ') Substatement [statement]
    construct EmptyList [repeat declaration_or_statement]
        _
    construct Block [block]
        '{ EmptyList '}
    construct ElseStatement [statement]
        Block
    construct ElseClause [else_clause]
        'else ElseStatement
    by
        'if '( Condition ') Substatement ElseClause
end rule

rule if_substatement_then_block
    replace [if_statement]
        'if '( Condition [expression] ') Substatement [statement] ElseClause [else_clause]
    deconstruct not Substatement
        Block [block]
    construct DeclarationOrStatement [declaration_or_statement]
        Substatement
    construct ThenList [repeat declaration_or_statement]
        _ [. DeclarationOrStatement]
    construct ThenBlock [block]
        '{ ThenList '}
    construct ThenStatement [statement]
        ThenBlock
    by
        'if '( Condition ') ThenStatement ElseClause
end rule

rule if_substatement_else_block
    replace [if_statement]
        'if '( Condition [expression] ') ThenClause [statement] ElseClause [else_clause]
    deconstruct ElseClause
        'else Substatement [statement]     
    deconstruct not Substatement
        Block [block]
    construct DeclarationOrStatement [declaration_or_statement]
        Substatement
    construct ElseList [repeat declaration_or_statement]
        _ [. DeclarationOrStatement]
    construct ElseBlock [block]
        '{ ElseList '}
    construct ElseStatement [statement]
        ElseBlock
    construct NewElseClause [else_clause]
        'else ElseStatement
    by
        'if '( Condition ') ThenClause NewElseClause
end rule
