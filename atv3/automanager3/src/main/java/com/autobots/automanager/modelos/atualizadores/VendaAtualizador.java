package com.autobots.automanager.modelos.atualizadores;

import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.modelos.outros.StringVerificadorNulo;

public class VendaAtualizador {
	
	private StringVerificadorNulo verificador = new StringVerificadorNulo();
	private UsuarioAtualizador usuarioAtualizador = new UsuarioAtualizador();
	private TelefoneAtualizador telefoneAtualizador = new TelefoneAtualizador();
	private DocumentoAtualizador documentoAtualizador = new DocumentoAtualizador();
	private EmailAtualizador emailAtualizador = new EmailAtualizador();

	private void atualizarDados(Venda venda, Venda atualizacao) {
		if (!verificador.verificar(atualizacao.getIdentificacao())) {
			venda.setIdentificacao(atualizacao.getIdentificacao());
		}

	}

	public void atualizar(Venda venda, Venda atualizacao) {
		atualizarDados(venda, atualizacao);
		usuarioAtualizador.atualizar(venda.getCliente(), atualizacao.getCliente());
		usuarioAtualizador.atualizar(venda.getFuncionario(), atualizacao.getFuncionario());
		telefoneAtualizador.atualizar(venda.getTelefones(), atualizacao.getTelefones());
		documentoAtualizador.atualizar(venda.getDocumentos(), atualizacao.getDocumentos());
		emailAtualizador.atualizar(venda.getEmails(), atualizacao.getEmails());
	}

}
