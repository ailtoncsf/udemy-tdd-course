

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrdemTest {

	/*
	 * Criando classe onde um teste depende da execução do outro.
	 * É necessário executá-los na ordem para que o teste seja realizado com sucesso.
	 * Dessa maneira, a anotação FixMethodOrder faz com que os testes sejam executados em ordem alfabética.
	 * Entretanto, esta não uma boa prática para construção de testes, pois é necessário que eles sejam independentes.
	 */
	public static int contador = 0;
	
	@Test
	public void inicia() {
		contador = 1;
	}
	
	@Test
	public void verifica() {
		Assert.assertEquals(1, contador);
	}
}
