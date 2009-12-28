package com.stabilit.pattern.proxy;

public class Proxy implements ObjektInterface {
	EigentlichesObjekt eigentlichesObjekt = null;

	@Override
	public void anfrage() {

		if (eigentlichesObjekt == null) {
			eigentlichesObjekt = new EigentlichesObjekt();
		}
		eigentlichesObjekt.anfrage();
	}
}
