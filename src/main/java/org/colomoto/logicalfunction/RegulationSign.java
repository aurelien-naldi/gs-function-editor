package org.colomoto.logicalfunction;

public enum RegulationSign {

	UNKNOWN, POSITIVE, NEGATIVE, DUAL;

    public String getSymbol() {
        switch (this) {
            case UNKNOWN:
                return "?";
            case POSITIVE:
                return "+";
            case NEGATIVE:
                return "-";
            case DUAL:
                return "~";
        }

        return "?";
    }
}
