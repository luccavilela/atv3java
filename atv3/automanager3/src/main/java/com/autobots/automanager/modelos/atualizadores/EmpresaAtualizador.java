package com.autobots.automanager.modelos.atualizadores;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.modelos.outros.StringVerificadorNulo;

public class EmpresaAtualizador {
	
	private StringVerificadorNulo verificador = new StringVerificadorNulo();
	private TelefoneAtualizador telefoneAtualizador = new TelefoneAtualizador();
	private EnderecoAtualizador enderecoAtualizador = new EnderecoAtualizador();
	private MercadoriaAtualizador mercadoriaAtualizador = new MercadoriaAtualizador();
	private ServicoAtualizador servicoAtualizador = new ServicoAtualizador();
	private VendaAtualizador vendaAtualizador = new VendaAtualizador();
	private UsuarioAtualizador usuarioAtualizador = new UsuarioAtualizador();

	private void atualizarDados(Empresa empresa, Empresa atualizacao) {
		if (!verificador.verificar(atualizacao.getRazaoSocial())) {
			empresa.setRazaoSocial(atualizacao.getRazaoSocial());
		}
		if (!verificador.verificar(atualizacao.getNomeFantasia())) {
			empresa.setNomeFantasia(atualizacao.getNomeFantasia());
		}
	}

	public void atualizar(Empresa empresa, Empresa atualizacao) {
		atualizarDados(empresa, atualizacao);
		telefoneAtualizador.atualizar(empresa.getTelefones(), atualizacao.getTelefones());
		enderecoAtualizador.atualizar(empresa.getEndereco(), atualizacao.getEndereco());
		mercadoriaAtualizador.atualizar(empresa.getMercadorias(), atualizacao.getMercadorias());
		servicoAtualizador.atualizar(empresa.getServicos(), atualizacao.getServicos());
		vendaAtualizador.atualizar(empresa.getVendas(), atualizacao.getVendas());
		usuarioAtualizador.atualizar(empresa.getUsuarios(), atualizacao.getUsuarios());
		
	}
}
