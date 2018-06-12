package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {

	private LocacaoDAO dao;
	
	private SPCService sPCservice;
	
	private EmailService emailService;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {

		Double valorTotal = 0.0;

		if (usuario == null) {
			throw new LocadoraException("Usuário vazio.");
		}

		if (filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme vazio.");
		}

		for (Filme item : filmes) {
			if (item.getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}
		}

		if(sPCservice.possuiNegativacao(usuario)) {
			throw new LocadoraException("Usuário Negativado.");
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);

		for (int i = 0; i < filmes.size(); i++ ) {
			Filme filme = filmes.get(i);
			Double valorFilme = filme.getPrecoLocacao();
			
			switch (i) {
				case 2: valorFilme = valorFilme * 0.75; break;
				case 3: valorFilme = valorFilme * 0.5; break;
				case 4: valorFilme = valorFilme * 0.25; break;
				case 5: valorFilme = 0d; break;
			}
				
			valorTotal += valorFilme;
		}		
		
		locacao.setValor(valorTotal);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = DataUtils.adicionarDias(dataEntrega, 1);
		}
		
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		dao.salvar(locacao);

		return locacao;
	}
	
	public void notificarAtrasos() {
		List<Locacao> locacoes = dao.obterLocacoesPendentes();
		
		for(Locacao locacao: locacoes) {
			emailService.notificarUsuario(locacao.getUsuario());
		}
	}
	
	public void setDao(LocacaoDAO dao) {
		this.dao = dao;
	}
	
	public void setSPCService(SPCService spc) {
		this.sPCservice = spc;
	}
	
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}
}