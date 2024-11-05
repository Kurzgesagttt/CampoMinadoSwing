package br.com.cod3r.cm.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Tabuleiro implements CampoObservador{
 private final int linhas;
 private final int colunas;
 private final int minas;
 
 private final List<Campo> campos = new ArrayList<Campo>();
 private final List<Consumer<ResultadoEvento>> observadores = 
	new ArrayList<>();
 
 public Tabuleiro(int linha, int coluna, int minas) {
	this.linhas = linha;
	this.colunas = coluna;
	this.minas = minas;
	
	gerarCampos();
	associarVizinhos();
	sortearMinas();
 }

 public void paraCadaCampo(Consumer<Campo> funcao) {
	 campos.forEach(funcao);
 }
 
 public void registrarObservador(Consumer<ResultadoEvento> observador) {
	 observadores.add(observador);
 }
 private void notificarObservadores(Boolean resultado) {
		observadores.stream().forEach(o -> o.
				accept(new ResultadoEvento(resultado)));
	}
 
 public void abrir(int linha,int coluna) {
	 try {
		 campos.parallelStream()
		 .filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
		 .findFirst().ifPresent(c -> c.abrir());;
	 }catch(Exception e) {
		 
		 //FIXME ajustar a implementacao do metodo abrir
		 campos.forEach(c -> c.setAberto(true));
		 throw e;
	 }
	
 }
 
 private void mostrarMinas() {
	 campos.stream().filter(c -> c.isMinado())
	 .forEach(c -> c.setAberto(true));
 }
 
 public void alternarMarcacao(int linha,int coluna) {
	 campos.parallelStream()
	 .filter(c -> c.getLinha() == linha && c.getColuna() == coluna)
	 .findFirst().ifPresent(c -> c.alternarMarcacao());;
 }
 
 
 private void gerarCampos() {
		for (int l =0; l < linhas; l++) {
			for(int c = 0; c < colunas; c++) {
				Campo campo = new Campo(l, c);
				campo.registrarObservador(this);
				campos.add(campo);
			}
		}
	 }
 
 private void associarVizinhos() {
		for(Campo c1:campos) {
			for(Campo c2:campos) {
				c1.adicionarVizinho(c2);
			}
		}
	}
 
 private void sortearMinas() {
	long minasArmadas = 0;
	Predicate<Campo> minado = c -> c.isMinado();
	do {
		minasArmadas = campos.stream().filter(minado).count();
		int aleatorio = (int) (Math.random() * campos.size());

		campos.get(aleatorio).minar();
		
	}while(minasArmadas < minas);	
}
 
 public boolean objetivoAlcancado(){
		return campos.stream().allMatch(c ->c.objetivoAlcancado());
	}
 
 public int getLinhas() {
	return linhas;
}

public int getColunas() {
	return colunas;
}

public void reiniciar() {
	 campos.stream().forEach(c -> c.reiniciar());
	 sortearMinas();
	 
 }

	public void eventoOcorreu(Campo campo, CampoEvento evento) {
		if(evento == CampoEvento.EXPLODIR) {
			mostrarMinas();
			notificarObservadores(false);
		}else if(objetivoAlcancado()) {
			notificarObservadores(true);
		}
		
	}
 
}