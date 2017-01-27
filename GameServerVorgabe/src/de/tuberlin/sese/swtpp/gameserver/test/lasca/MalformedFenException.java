package de.tuberlin.sese.swtpp.gameserver.test.lasca;

public class MalformedFenException extends RuntimeException {

	public MalformedFenException(){
		
	}
	
	public MalformedFenException(String error){
		super(error);
	}
	
}
