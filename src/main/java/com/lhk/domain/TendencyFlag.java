package com.lhk.domain;

public enum TendencyFlag {
	POSITIVE,
	NEUTRAL,
	NEGATIVE
	;
	
	public static TendencyFlag parse(String tendencyFlag){
		switch(tendencyFlag){
		case "POSITIVE":
			return POSITIVE;
		case "正面":
		    return POSITIVE;
		case "NEUTRAL":
			return NEUTRAL;
        case "中性":
            return NEUTRAL;
        case "NEGATIVE":
            return NEGATIVE;
		case "负面":
			return NEGATIVE;
		}
		throw new IllegalArgumentException("TendencyFlag parse exception: "+tendencyFlag);
	}
}
