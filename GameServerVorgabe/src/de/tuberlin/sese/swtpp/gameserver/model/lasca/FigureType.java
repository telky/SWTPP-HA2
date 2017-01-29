package de.tuberlin.sese.swtpp.gameserver.model.lasca;

import java.io.Serializable;

public enum FigureType {
	EMPTY ("__"),
	WHITE_SOLDIER ("w"),
	WHITE_OFFICER ("W"),
	BLACK_SOLDIER ("b"),
	BLACK_OFFICER ("B");
	
	private final String name;       

    private FigureType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false 
        return name.equals(otherName);
    }

    public String toBoardName() {
       return this.name;
    }
}

