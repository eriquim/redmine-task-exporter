package br.jus.tjce.releasecontroller.util;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {
	
	 
    /** 
     * Método que pega a lista de nomes completos e retorna 
     * apenas o primeiro nome de cada item da lista em apenas um string
     * 
     * @param responsaveis
     * @return String [nomes separados por ","]
     */
    public static String tratarNomes(List<String> responsaveis) {
    	if (responsaveis == null || responsaveis.isEmpty()) {
            return "";
        }
        return responsaveis.stream()
                .filter(nome -> nome != null && !nome.trim().isEmpty())
                .map(nome -> tratarNome(nome)) // pega apenas o primeiro nome
                .collect(Collectors.joining(", "));
    }
    
    /** 
     * Método que pega nomes completos e retorna 
     * apenas o primeiro nome em string
     * 
     * @param nome
     * @return String
     */
    public static String tratarNome(String nome) {
    	if(nome != null && !nome.isEmpty()) {
    		return nome.trim().split("\\s+")[0];
    	} else {
    		return null;
    	}
    }
    
    public static String gerarEspacos(int quantidade) {
        if (quantidade <= 0) {
            return "";
        }
        return new String(new char[quantidade*3]).replace('\0', ' ');
    }

}
