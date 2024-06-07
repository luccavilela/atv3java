package com.autobots.automanager.modelos.atualizadores;

import java.util.Set;

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.modelos.outros.StringVerificadorNulo;

public class UsuarioAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();
	private EnderecoAtualizador enderecoAtualizador = new EnderecoAtualizador();
	private DocumentoAtualizador documentoAtualizador = new DocumentoAtualizador();
	private TelefoneAtualizador telefoneAtualizador = new TelefoneAtualizador();

	private void atualizarDados(Usuario usuario, Usuario atualizacao) {
		if (!verificador.verificar(atualizacao.getNome())) {
			usuario.setNome(atualizacao.getNome());
		}
		if (!verificador.verificar(atualizacao.getNomeSocial())) {
			usuario.setNomeSocial(atualizacao.getNomeSocial());
		}
	}

	public void atualizar(Usuario usuario, Usuario atualizacao) {
		atualizarDados(usuario, atualizacao);
		enderecoAtualizador.atualizar(usuario.getEndereco(), atualizacao.getEndereco());
		documentoAtualizador.atualizar(usuario.getDocumentos(), atualizacao.getDocumentos());
		telefoneAtualizador.atualizar(usuario.getTelefones(), atualizacao.getTelefones());
	}
	
	public void atualizar(Set<Usuario> usuarios, Set<Usuario> atualizacoes) {
		for (Usuario atualizacao : atualizacoes) {
			for (Usuario usuario : usuarios) {
				if (atualizacao.getId() != null) {
					if (atualizacao.getId() == usuario.getId()) {
						atualizar(usuario, atualizacao);
					}
				}
			}
		}
	}
}
