package br.com.cod3r.cm.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Campo {
	
	private final int coluna;
	private final int linha;
	
	private boolean aberto = false;
	private boolean minado = false ;
	private boolean marcado = false;
	
	private List<Campo> vizinhos = new ArrayList<>();
	private List<CampoObservador> observadores = new ArrayList<CampoObservador>();
	
	
	public Campo(int linha, int coluna){
		this.linha = linha;
		this.coluna = coluna;
	}
	
	public void registrarObservador(CampoObservador observador) {
		observadores.add(observador);
	}
	
	private void notificarObservadores(CampoEvento evento) {
		observadores.stream().forEach(o -> o.eventoOcorreu(this, evento));
	}
	
	public boolean adicionarVizinho(Campo vizinho){
	    boolean linhaDiferente = linha != vizinho.linha;
	    boolean colunaDiferente = coluna != vizinho.coluna;
	    boolean diagonal = linhaDiferente && colunaDiferente;
	    
	    int deltaLinha = Math.abs(linha - vizinho.linha);
	    int deltaColuna = Math.abs(coluna - vizinho.coluna);
	    int deltaGeral = deltaLinha + deltaColuna; // Corrigido para somar deltaLinha e deltaColuna
	    
	    if(deltaGeral == 1 && !diagonal) {
	        vizinhos.add(vizinho);
	        return true;
	    } else if(deltaGeral == 2 && diagonal) {
	        vizinhos.add(vizinho);
	        return true;
	    }
	    return false;
	}

	public void alternarMarcacao() {
		if(!aberto) {
			marcado = !marcado;
			
			if(marcado) {
				notificarObservadores(CampoEvento.MARCAR);
			}else {
				notificarObservadores(CampoEvento.DESMARCAR);
			}
		}
	}
		
	public boolean abrir() {
		
		if(!aberto && !marcado) {
			if(minado) {
				
				notificarObservadores(CampoEvento.EXPLODIR);
				return true;
			}
			setAberto(true);
			
			if(vizinhancaSegura()) {
				vizinhos.forEach(v -> v.abrir());
			}
			return true;
		}else {
			return false;
		}
	}
	
	boolean vizinhancaSegura() {
		return vizinhos.stream().noneMatch(v -> v.minado);
	}
	
	public boolean isMarcado() {
		return marcado;
	}
	public void minar() {
		if(!minado) {
			minado = true;
		}
	}
	
	public void setAberto(boolean aberto) {
		this.aberto = aberto;
		if(aberto) {
			notificarObservadores(CampoEvento.ABRIR);
		}
		
	}
	
	public boolean isMinado() {
		return minado;
	}
	public boolean isFechado() {
		return !isAberto();
	}
	
	public boolean isAberto() {
		return aberto;
	}

	public int getColuna() {
		return coluna;
	}

	public int getLinha() {
		return linha;
	}
	//testar
	public boolean objetivoAlcancado() {
		boolean desvendado = !minado && aberto;
		boolean protegido = minado && marcado;
		
		return desvendado || protegido;
	}
	
	long minasNaVizinhanca() {
		return vizinhos.stream().filter(v -> v.minado).count();
	}
	void reiniciar() {
		aberto = false;
		minado = false;
		marcado = false;
	}
}