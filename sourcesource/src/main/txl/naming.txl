include "2-identify.txl"

redefine declaration_or_statement
      [opt id] [NL] [local_variable_declaration] 
    | [opt id] [NL] [class_declaration]
    | [opt id] [NL] [statement]
end redefine

function main
    replace [program]
        P [program]
    by
        P [explicate] [identify]
end function
