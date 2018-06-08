package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
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
	
	private LocacaoService service;
	
	private static int contador = 0;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup() {
		System.out.println("Antes");
		service = new LocacaoService();
		contador++;
		System.out.println(contador);
	}
	
	@After
	public void tearDown() {
		System.out.println("Depois");
	}
	
	@BeforeClass
	public static void setupClass() {
		System.out.println("Antes da classe");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println("Depois da classe");
	}	
	
	
	@Test
	public void testeLocacao() throws Exception {
		
		//cenario		
		Usuario usuario = new Usuario("Usuário 1");
		Filme filme = new Filme("Filme1", 2, 5.0);
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filme);
		
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));
	}
	
	@Test(expected=FilmeSemEstoqueException.class)
	public void testeLocacao_filmeSemEstoque_elegante() throws Exception {
		/* [Utilizando a forma elegante]
		 * 
		 * Funciona bem quando apenas a excessão interessa e 
		 * quando conseguimos garantir o motivo pelo qual a exceção foi lançada.
		 * mas caso seja necessário exibir a mensagem é melhor utilizar a forma robusta ou nova.
		 * 
		 */
		
		
		//cenario
		Usuario usuario = new Usuario("Usuário 1");
		Filme filme = new Filme("Filme 2", 0, 4.0);
		
		//acao
		service.alugarFilme(usuario, filme);
	}	
	
	@Test
	public void testeLocacao_usuarioVazio() throws FilmeSemEstoqueException {
		/* [Utilizando a forma robusta]
		 * Nesta forma sempre colocar o assertFail.
		 * 
		 * *************************************************************
		 * Seu uso é recomendado por ser mais completa				   *
		 * E possibilitar flexibilidade na manipuação das informações. *
		 * *************************************************************
		 */
		
		
		//cenario
		Filme filme = new Filme("Filme 2", 1, 4.0);
		
		//acao
		try {
			service.alugarFilme(null, filme);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuário vazio."));
		}
		
		System.out.println("Forma robusta.");
	}
	
	@Test
	public void testeLocacao_FilmeVazio() throws FilmeSemEstoqueException, LocadoraException {
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
		
		System.out.println("Forma nova.");
		
	}
}
