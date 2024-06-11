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
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.modelos.atualizadores.ServicoAtualizador;
import com.autobots.automanager.modelos.links.AdicionadorLinkServico;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.ServicoRepositorio;
import com.autobots.automanager.repositorios.VendaRepositorio;

@RestController
@RequestMapping("/servico")
public class ServicoControle {
	
	@Autowired 
	private ServicoRepositorio repositorioServico;
	
	@Autowired
	private VendaRepositorio repositorioVenda;
	
	@Autowired
	private RepositorioEmpresa repositorioEmpresa;
	
	@Autowired
	private AdicionadorLinkServico adicionadorLink;
	
	@GetMapping("/servico/{servicoId}")
	public ResponseEntity<Servico> obterServico(@PathVariable Long servicoId) {
	    try {
	        Servico servico = repositorioServico.findById(servicoId).orElseThrow(() -> new EntityNotFoundException("Servico não encontrado"));  
	        adicionadorLink.adicionarLink(servico);
	        
	        return new ResponseEntity<>(servico, HttpStatus.FOUND);
	    } catch (EntityNotFoundException | NoSuchElementException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("servicos")
	public ResponseEntity<List<Servico>> obterServicos() {
		List<Servico> servicos = repositorioServico.findAll();
		if (servicos.isEmpty()) {
			ResponseEntity<List<Servico>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(servicos);
			ResponseEntity<List<Servico>> resposta = new ResponseEntity<>(servicos, HttpStatus.FOUND);
			return resposta;
		}
	}
	
	@PostMapping("servico/cadastro")
	public ResponseEntity<?> cadastrarServico(@RequestBody Servico servico) {
		HttpStatus status = HttpStatus.CONFLICT;
		if (servico.getId() == null) {
			repositorioServico.save(servico);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(status);

	}
	
	@PutMapping("/atualizar/{servicoId}")
    public ResponseEntity<?> atualizarServico(@PathVariable Long servicoId, @RequestBody Servico novoServico) {
		try {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			Servico servico = repositorioServico.getById(servicoId);
			if (servico != null) {
				ServicoAtualizador atualizador = new ServicoAtualizador();
				atualizador.atualizar(servico, novoServico);
            
				repositorioServico.save(servico);
				status = HttpStatus.OK;
			} 
			return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
    }
	
	@DeleteMapping("/excluir/{servicoId}")
	public ResponseEntity<?> excluirServico(@PathVariable Long servicoId) {
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    Servico servico = repositorioServico.getById(servicoId);
	    if (servico != null) {
	        List<Venda> vendas = repositorioVenda.findAll();
	        for (Venda venda : vendas) {
	            if (venda.getServicos().contains(servico)) {
	                venda.getServicos().remove(servico);
	                repositorioVenda.save(venda);
	            }
	        }
	        
	        List<Empresa> empresas = repositorioEmpresa.findAll();
	        for (Empresa empresa : empresas) {
	            if (empresa.getServicos().contains(servico)) {
	                empresa.getServicos().remove(servico);
	                repositorioEmpresa.save(empresa);
	            }
	        }
	        
	        repositorioServico.deleteById(servicoId);
	        status = HttpStatus.OK;
	    }
	    return new ResponseEntity<>(status);
	}
}
