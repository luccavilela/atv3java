package com.autobots.automanager.modelos.atualizadores;

import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.modelos.outros.StringVerificadorNulo;

public class CredencialAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();
	
	public void atualizar(CredencialUsuarioSenha credencial, CredencialUsuarioSenha novaCredencial) {
		if (!verificador.verificar(novaCredencial.getNomeUsuario().toString())) { 
			credencial.setNomeUsuario(novaCredencial.getNomeUsuario());
		}
		if (!verificador.verificar(novaCredencial.getSenha().toString())) { 
			credencial.setSenha(novaCredencial.getSenha());
		}
	}
}
