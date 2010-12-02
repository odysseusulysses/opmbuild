function add_records_to_switch_statement Occurrence [declaration_or_statement] Point [id]%% Occurrence is the original Occurrence as it suggests, but then these rules are intended to create record 
%%statements and then stick them infront/after the 
    replace [statement]
        SwitchStatement [switch_statement]
    deconstruct SwitchStatement
        'switch '( Express [expression] ') SwitchBlock [switch_block]
    export Express
    deconstruct SwitchBlock
	'{ SwitchAlternative [repeat switch_alternative] '}
    construct newSwitchAlternative [repeat switch_alternative]
	SwitchAlternative [add_records_to_switch_alternatives Occurrence Point]
    construct NewSwitch [switch_block]
	'{ newSwitchAlternative '}
    construct new_switch_statement [switch_statement]
	'switch '( Express ') NewSwitch
    by
        new_switch_statement
end function

function add_records_to_switch_alternatives Occurrence [declaration_or_statement] Point [id]
    replace [repeat switch_alternative]
	First [switch_alternative] Rest [repeat switch_alternative]
    by
	First [add_records_to_switch_alternative Occurrence Point] Rest [add_records_to_switch_alternatives Occurrence Point]
end function

function add_records_to_switch_alternative Occurrence [declaration_or_statement] Point [id]
    replace [switch_alternative]
	Label [switch_label] DecStat [block]
    import Express [expression]
    construct StringExpress [id]
	Point [reparse Express]
    construct NewStatement [charlit]
        _ [+ "Recorder._currentRecorder.used ("][+ ''"'][+ Point][+ ''"'][+ ", "][+ StringExpress][+ ", "][+ ''"'][+ "Execution Cause By"][+ ''"'][+ ");"]
    construct CauseStatement [repeat declaration_or_statement]
        Occurrence [parse NewStatement]
    construct NewDecStat [block]
	DecStat [add_records_to_block CauseStatement]
    by
	Label NewDecStat
end function
