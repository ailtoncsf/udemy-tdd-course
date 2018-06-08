package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

public class LocacaoService {

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

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);

		for (Filme item : filmes) {
			valorTotal += item.getPrecoLocacao();
		}
		
		locacao.setValor(valorTotal);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		// TODO adicionar método para salvar

		return locacao;
	}
}