package com.stabilit.pattern.proxy;

public class EigentlichesObjekt implements ObjektInterface {

	@Override
	public void anfrage() {
		System.out.println("Eigentliches Objekt anfrage ausgeführt");
	}

}
