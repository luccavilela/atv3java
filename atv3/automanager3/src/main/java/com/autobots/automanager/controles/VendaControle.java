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

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.modelos.atualizadores.VendaAtualizador;
import com.autobots.automanager.modelos.links.AdicionadorLinkVenda;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.VeiculoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;



@RestController
@RequestMapping("/venda")
public class VendaControle {
	
	@Autowired 
	private VendaRepositorio repositorioVenda;
	
	@Autowired
	private UsuarioRepositorio repositorioUsuario;
	
	@Autowired
	private VeiculoRepositorio repositorioVeiculo;
	
	@Autowired
	private RepositorioEmpresa repositorioEmpresa;
	
	@Autowired
	private AdicionadorLinkVenda adicionadorLink;
	
	@GetMapping("/venda/{vendaId}")
	public ResponseEntity<Venda> obterVenda(@PathVariable Long vendaId) {
	    try {
	        Venda venda = repositorioVenda.findById(vendaId).orElseThrow(() -> new EntityNotFoundException("Venda não encontrada"));  
	        adicionadorLink.adicionarLink(venda);
	        
	        return new ResponseEntity<>(venda, HttpStatus.FOUND);
	    } catch (EntityNotFoundException | NoSuchElementException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("vendas")
	public ResponseEntity<List<Venda>> obterVendas() {
		List<Venda> vendas = repositorioVenda.findAll();
		if (vendas.isEmpty()) {
			ResponseEntity<List<Venda>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(vendas);
			ResponseEntity<List<Venda>> resposta = new ResponseEntity<>(vendas, HttpStatus.FOUND);
			return resposta;
		}
	}
	
	@PostMapping("venda/cadastro")
	public ResponseEntity<?> cadastrarVenda(@RequestBody Venda venda) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (venda.getId() == null) {
			repositorioVenda.save(venda);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(status);

	}
	
	@PutMapping("/atualizar/{vendaId}")
    public ResponseEntity<?> atualizarVenda(@PathVariable Long vendaId, @RequestBody Venda novaVenda) {
		try {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			Venda venda = repositorioVenda.getById(vendaId);
			if (venda != null) {
				VendaAtualizador atualizador = new VendaAtualizador();
				atualizador.atualizar(venda, novaVenda);
            
				repositorioVenda.save(venda);
				status = HttpStatus.OK;
			} 
			return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
    }
	
	@DeleteMapping("/excluir/{vendaId}")
	public ResponseEntity<?> excluirVenda(@PathVariable Long vendaId) {
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    Venda venda = repositorioVenda.getById(vendaId);
	    if (venda != null) {
	        List<Usuario> usuarios = repositorioUsuario.findAll();
	        for (Usuario usuario : usuarios) {
	            if (usuario.getVendas().contains(venda)) {
	                usuario.getVendas().remove(venda);
	                repositorioUsuario.save(usuario);
	            }
	        }
	        
	        List<Veiculo> veiculos = repositorioVeiculo.findAll();
	        for (Veiculo veiculo : veiculos) {
	            if (veiculo.getVendas().contains(venda)) {
	                veiculo.getVendas().remove(venda);
	                repositorioVeiculo.save(veiculo);
	            }
	        }
	        
	        List<Empresa> empresas = repositorioEmpresa.findAll();
	        for (Empresa empresa : empresas) {
	            if (empresa.getVendas().contains(venda)) {
	                empresa.getVendas().remove(venda);
	                repositorioEmpresa.save(empresa);
	            }
	        }
	        
	        repositorioVenda.deleteById(vendaId);
	        status = HttpStatus.OK;
	    }
	    return new ResponseEntity<>(status);
	}
}
