package com.autobots.automanager.controles;

import java.util.List;
import java.util.NoSuchElementException;

import javax.persistence.EntityNotFoundException;

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

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.modelos.atualizadores.VeiculoAtualizador;
import com.autobots.automanager.modelos.links.AdicionadorLinkVeiculo;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VeiculoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@RestController
@RequestMapping("/veiculo")
public class VeiculoControle {
	@Autowired
	VeiculoRepositorio repositorioVeiculo;
	@Autowired
	private VendaRepositorio repositorioVenda;
	@Autowired
	private UsuarioRepositorio repositorioUsuario;
	
	@Autowired
	private AdicionadorLinkVeiculo adicionadorLink;
	
	
	@GetMapping("/veiculo/{veiculoId}")
	public ResponseEntity<Veiculo> obterVeiculo(@PathVariable Long veiculoId) {
	    try {
	        Veiculo veiculo = repositorioVeiculo.findById(veiculoId).orElseThrow(() -> new EntityNotFoundException("Veiculo não encontrado"));  
	        adicionadorLink.adicionarLink(veiculo);
	        
	        return new ResponseEntity<>(veiculo, HttpStatus.FOUND);
	    } catch (EntityNotFoundException | NoSuchElementException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	@GetMapping("veiculos")
	public ResponseEntity<List<Veiculo>> obterVeiculos() {
		List<Veiculo> veiculos = repositorioVeiculo.findAll();
		if (veiculos.isEmpty()) {
			ResponseEntity<List<Veiculo>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(veiculos);
			ResponseEntity<List<Veiculo>> resposta = new ResponseEntity<>(veiculos, HttpStatus.FOUND);
			return resposta;
		}
	
	}
	
	@PostMapping("veiculo/cadastro")
	public ResponseEntity<?> cadastrarVeiculo(@RequestBody Veiculo veiculo) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (veiculo.getId() == null) {
			repositorioVeiculo.save(veiculo);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(status);

	}
	
	@PutMapping("/atualizar/{veiculoId}")
	public ResponseEntity<?> atualizarVeiculo(@PathVariable Long veiculoId, @RequestBody Veiculo novoVeiculo) {
		try {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			Veiculo veiculo = repositorioVeiculo.getById(veiculoId);
			if (veiculo != null) {
				VeiculoAtualizador atualizador = new VeiculoAtualizador();
				atualizador.atualizar(veiculo, novoVeiculo);
				repositorioVeiculo.save(veiculo);
				status = HttpStatus.OK;
			} else {
				status = HttpStatus.BAD_REQUEST;
			}
			return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	@DeleteMapping("/excluir/{veiculoId}")
	public ResponseEntity<?> excluirVeiculo(@PathVariable Long veiculoId) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		Veiculo veiculo = repositorioVeiculo.getById(veiculoId);
		if (veiculo != null) {
			List<Venda> vendas = repositorioVenda.findAll();
			for (Venda venda : vendas) {
				if (venda.getVeiculo() != null && venda.getVeiculo().getId().equals(veiculoId)) {
					venda.setVeiculo(null);
					repositorioVenda.save(venda);
				}
			}

			List<Usuario> usuarios = repositorioUsuario.findAll();
			for (Usuario usuario : usuarios) {
				if (usuario.getVeiculos().contains(veiculo)) {
					usuario.getVeiculos().remove(veiculo);
					repositorioUsuario.save(usuario);
				}
			}
			repositorioVeiculo.deleteById(veiculoId);
			status = HttpStatus.OK;
		}
		return new ResponseEntity<>(status);
	}

}
