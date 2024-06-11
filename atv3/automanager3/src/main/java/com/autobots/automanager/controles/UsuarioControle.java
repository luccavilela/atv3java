package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.modelos.atualizadores.UsuarioAtualizador;
import com.autobots.automanager.modelos.links.AdicionadorLinkUsuario;
import com.autobots.automanager.modelos.outros.UsuarioSelecionador;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VeiculoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;


@RestController
@RequestMapping("/usuario")
public class UsuarioControle {
	@Autowired
	private UsuarioRepositorio repositorio;
	@Autowired
	private RepositorioEmpresa empresaRepositorio;
	@Autowired
	private VendaRepositorio repositorioVenda;
	@Autowired
	private VeiculoRepositorio repositorioVeiculo;
	@Autowired
	private UsuarioSelecionador selecionador;
	@Autowired
	private AdicionadorLinkUsuario adicionadorLink;

	@GetMapping("/usuario/{id}")
	public ResponseEntity<Usuario> obterUsuario(@PathVariable long id) {
		List<Usuario> usuarios = repositorio.findAll();
		Usuario usuario = selecionador.selecionar(usuarios, id);
		if (usuario == null) {
			ResponseEntity<Usuario> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(usuario);
			ResponseEntity<Usuario> resposta = new ResponseEntity<Usuario>(usuario, HttpStatus.FOUND);
			return resposta;
		}
	}

	@GetMapping("/usuarios")
	public ResponseEntity<List<Usuario>> obterUsuarios() {
		List<Usuario> usuarios = repositorio.findAll();
		if (usuarios.isEmpty()) {
			ResponseEntity<List<Usuario>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(usuarios);
			ResponseEntity<List<Usuario>> resposta = new ResponseEntity<>(usuarios, HttpStatus.FOUND);
			return resposta;
		}
	}

	@PostMapping("/usuario/cadastro")
	public ResponseEntity<?> cadastrarCliente(@RequestBody Usuario usuario) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (usuario.getId() == null) {
			repositorio.save(usuario);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(status);

	}

	@PutMapping("/usuario/atualizar")
	public ResponseEntity<?> atualizarCliente(@RequestBody Usuario atualizacao) {
		HttpStatus status = HttpStatus.CONFLICT;
		Usuario usuario = repositorio.getById(atualizacao.getId());
		if (usuario != null) {
			UsuarioAtualizador atualizador = new UsuarioAtualizador();
			atualizador.atualizar(usuario, atualizacao);
			repositorio.save(usuario);
			status = HttpStatus.OK;
		} else {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(status);
	}

	@DeleteMapping("/usuario/excluir")
	public ResponseEntity<?> excluirUsuario(@RequestBody Usuario exclusao) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Usuario usuario = repositorio.getById(exclusao.getId());
		if (usuario != null) {
			
			List<Empresa> empresas = empresaRepositorio.findByUsuariosId(usuario.getId());
			for (Empresa empresa : empresas) {
				empresa.getUsuarios().remove(usuario);
				empresaRepositorio.save(empresa);
			}

			
			List<Venda> vendas = repositorioVenda.findAll();
			for (Venda venda : vendas) {
				if (venda.getCliente() != null && venda.getCliente().getId().equals(usuario.getId())) {
					venda.setCliente(null);
				}
				if (venda.getFuncionario() != null && venda.getFuncionario().getId().equals(usuario.getId())) {
					venda.setFuncionario(null);
				}
				repositorioVenda.save(venda);
			}

			
			List<Veiculo> veiculos = repositorioVeiculo.findAll();
			for (Veiculo veiculo : veiculos) {
				if (veiculo.getProprietario() != null && veiculo.getProprietario().getId().equals(usuario.getId())) {
					veiculo.setProprietario(null);
				}
				repositorioVeiculo.save(veiculo);
			}

	
			repositorio.deleteById(exclusao.getId());
			status = HttpStatus.OK;
		}
		return new ResponseEntity<>(status);
	}
}
