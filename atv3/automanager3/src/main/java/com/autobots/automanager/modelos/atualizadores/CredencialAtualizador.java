package com.autobots.automanager.modelos.atualizadores;

import com.autobots.automanager.entidades.Credencial;

public class CredencialAtualizador {
	public void atualizar(Credencial credencial, Credencial novaCredencial) {
		credencial.setCriacao(novaCredencial.getCriacao());
		credencial.setUltimoAcesso(novaCredencial.getUltimoAcesso());
		credencial.setInativo(novaCredencial.isInativo());
	}
}
