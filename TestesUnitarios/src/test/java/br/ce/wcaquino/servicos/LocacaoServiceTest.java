package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.builders.FilmeBuilder.umFilme;
import static br.ce.wcaquino.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.ce.wcaquino.builders.UsuarioBuilder.umUsuario;
import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHoje;
import static br.ce.wcaquino.matchers.MatchersProprios.ehHojeComDieferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.ce.wcaquino.daos.LocacaoDAO;
import br.ce.wcaquino.daos.LocacaoDAOFake;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import buildermaster.BuilderMaster;

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
		LocacaoDAO dao = Mockito.mock(LocacaoDAO.class);
		service.setDao(dao);
	}	
	
	@Test
	public void deveAlugarFilme() throws Exception {
		
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario		
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = new ArrayList<Filme>();
		
		filmes.add(umFilme().comValor(5.0).agora());
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(locacao.getDataLocacao(), ehHoje());
		error.checkThat(locacao.getDataRetorno(), ehHojeComDieferencaDias(1));
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
		Usuario usuario = umUsuario().agora();
		List<Filme> filme = new ArrayList<Filme>();
		
		filme.add(umFilmeSemEstoque().agora());
		//filme.add(new Filme("Filme 2", 0, 2.0));
		//filme.add(new Filme("Filme 3", 0, 2.0));
		
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
		
		filme.add(umFilme().agora());
		//filme.add(new Filme("Filme 2", 2, 2.0));
		//filme.add(new Filme("Filme 3", 2, 2.0));
		
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
		Usuario usuario = umUsuario().agora();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio.");

		//acao
		service.alugarFilme(usuario, null);
	}
		
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
		//assumindo que o dia é sabado, este teste será executado.
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filme = new ArrayList<Filme>();
		filme.add(umFilme().agora());
		
		//acao
		Locacao resultado = service.alugarFilme(usuario, filme);
		
		//verificacao
		assertThat(resultado.getDataRetorno(), caiNumaSegunda());
	}
	
	public static void main(String[] args) {
		new BuilderMaster().gerarCodigoClasse(Locacao.class);
	}
}
