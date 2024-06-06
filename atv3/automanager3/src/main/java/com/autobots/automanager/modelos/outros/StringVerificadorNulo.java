package com.autobots.automanager.modelos.outros;

import java.util.Date;

public class StringVerificadorNulo {

	public boolean verificar(String dado) {
		boolean nulo = true;
		if (!(dado == null)) {
			if (!dado.isBlank()) {
				nulo = false;
			}
		}
		return nulo;
	}
	
	public boolean verificar(Double valor) {
		return valor == null;
	}
	
	public boolean verificar(Date data) {
        return data == null;
    }
	
	public boolean verificar(Long valor) {
        return valor == null;
    }
}