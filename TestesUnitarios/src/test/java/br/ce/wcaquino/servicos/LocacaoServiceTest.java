package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

public class LocacaoServiceTest {
	
	/**************************************************************************************************************************************************
	 * Caso seja nececssário utilizar valores de atributos entre testes criar então atributos estáticos pois estes não são reinicializados pelo Junit
	 **************************************************************************************************************************************************/
	
	private LocacaoService service;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		service = new LocacaoService();
	}	
	
	@Test
	public void deveAlugarFilme() throws Exception {
		
		//cenario		
		Usuario usuario = new Usuario("Usuário 1");
		List<Filme> filmes = new ArrayList<Filme>();
		
		filmes.add(new Filme("Filme 1", 2, 5.0));
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
	}
	
	@Test(expected=FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		/* [Utilizando a forma elegante]
		 * 
		 * Funciona bem quando apenas a excessão interessa e 
		 * quando conseguimos garantir o motivo pelo qual a exceção foi lançada.
		 * mas caso seja necessário exibir a mensagem é melhor utilizar a forma robusta ou nova.
		 * 
		 */
		
		//cenario
		Usuario usuario = new Usuario("Usuário 1");
		List<Filme> filme = new ArrayList<Filme>();
		
		filme.add(new Filme("Filme 1", 0, 1.0));
		filme.add(new Filme("Filme 2", 0, 2.0));
		filme.add(new Filme("Filme 3", 0, 2.0));
		
		//acao
		service.alugarFilme(usuario, filme);
	}	
	
	@Test
	public void naoDeveAlugarFilmesSemUsuario() throws FilmeSemEstoqueException {
		/* [Utilizando a forma robusta]
		 * Nesta forma sempre colocar o assertFail.
		 * 
		 * *************************************************************
		 * Seu uso é recomendado por ser mais completa				   *
		 * E possibilitar flexibilidade na manipuação das informações. *
		 * *************************************************************
		 */
		
		
		//cenario
		List<Filme> filme = new ArrayList<Filme>();
		
		filme.add(new Filme("Filme 1", 2, 1.0));
		filme.add(new Filme("Filme 2", 2, 2.0));
		filme.add(new Filme("Filme 3", 2, 2.0));
		
		//acao
		try {
			service.alugarFilme(null, filme);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuário vazio."));
		}
	}
	
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException {
		/* [Utilizando a forma nova]
		 * 
		 * Funciona bem na maioria dos casos, mas existirão situações que apenas a forma robusta ajudará.
		 * [Verificar quais são estas situações]			
		 */
		
		//cenario
		Usuario usuario = new Usuario("Usuário 1");
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio.");

		//acao
		service.alugarFilme(usuario, null);
	}
	
	@Test
	public void devePagar75PctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
		
		//cenario
		Usuario usuario = new Usuario("Usuário 1");
		List<Filme> filme = new ArrayList<Filme>();
		filme.add(new Filme("Filme 1", 2, 4.0));
		filme.add(new Filme("Filme 2", 2, 4.0));
		filme.add(new Filme("Filme 3", 2, 4.0));
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filme);
		
		//verificacao
		assertThat(resultado.getValor(), is(11.0));
	}
	
	@Test
	public void devePagar50PctNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
		
		//cenario
		Usuario usuario = new Usuario("Usuário 1");
		List<Filme> filme = new ArrayList<Filme>();
		filme.add(new Filme("Filme 1", 2, 4.0));
		filme.add(new Filme("Filme 2", 2, 4.0));
		filme.add(new Filme("Filme 3", 2, 4.0));
		filme.add(new Filme("Filme 4", 2, 4.0));
		//4+4+3+2
		//acao
		Locacao resultado = service.alugarFilme(usuario, filme);
		
		//verificacao
		assertThat(resultado.getValor(), is(13.0));
	}
	
	@Test
	public void devePagar25PctNoFilme5() throws FilmeSemEstoqueException, LocadoraException {
		
		//cenario
		Usuario usuario = new Usuario("Usuário 1");
		List<Filme> filme = new ArrayList<Filme>();
		filme.add(new Filme("Filme 1", 2, 4.0));
		filme.add(new Filme("Filme 2", 2, 4.0));
		filme.add(new Filme("Filme 3", 2, 4.0));
		filme.add(new Filme("Filme 4", 2, 4.0));
		filme.add(new Filme("Filme 5", 2, 4.0));
		//4+4+3+2+1
		//acao
		Locacao resultado = service.alugarFilme(usuario, filme);
		
		//verificacao
		assertThat(resultado.getValor(), is(14.0));
	}
	
	@Test
	public void devePagar0PctNoFilme6() throws FilmeSemEstoqueException, LocadoraException {
		
		//cenario
		Usuario usuario = new Usuario("Usuário 1");
		List<Filme> filme = new ArrayList<Filme>();
		filme.add(new Filme("Filme 1", 2, 4.0));
		filme.add(new Filme("Filme 2", 2, 4.0));
		filme.add(new Filme("Filme 3", 2, 4.0));
		filme.add(new Filme("Filme 4", 2, 4.0));
		filme.add(new Filme("Filme 5", 2, 4.0));
		filme.add(new Filme("Filme 6", 2, 4.0));
		//4+4+3+2+1+0
		//acao
		Locacao resultado = service.alugarFilme(usuario, filme);
		
		//verificacao
		assertThat(resultado.getValor(), is(14.0));
	}
}
