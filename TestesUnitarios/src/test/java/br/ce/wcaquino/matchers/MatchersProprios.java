package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchersProprios {

	public static DiaSemanaMatcher caiEm(Integer diaSemana) {
		return new DiaSemanaMatcher(diaSemana);
	}
	
	public static DiaSemanaMatcher caiNumaSegunda() {
		return new DiaSemanaMatcher(Calendar.MONDAY);
	}
	
	public static DiferencaDiasMatcher ehHojeComDieferencaDias(Integer qtdDias) {
		return new DiferencaDiasMatcher(qtdDias);
	}
	
	public static DiferencaDiasMatcher ehHoje() {
		return new DiferencaDiasMatcher(0);
	}
}
