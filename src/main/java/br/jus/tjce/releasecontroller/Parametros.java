/**
 * 
 */
package br.jus.tjce.releasecontroller;

/**
 * @author Eric Moura
 *
 */
public class Parametros {
	
	//Git
	public static final String GITLAB_URL = "https://git.tjce.jus.br/api/v4";
	//public static final String PRIVATE_TOKEN = "";
	public static final String PROJECT_ID = "318"; // ID ou namespace codificado do projeto
	public static final int MR_IID = 972; // número interno do MR
	public static final String caminho = "fluxos_novos.txt";
	
	//Redmine
	public static final String API_KEY = "1e681c6f91688d022cac22c980c98c66022176ba";
    public static final Integer ISSUE_PAI = 160377;
    public static final String NOME_ARQUIVO = "tarefas_demanda.txt";
    public static final Boolean IMPRIMIR_CONSOLE = Boolean.TRUE;
    public static final String URL_BASE = "https://redmine.tjce.jus.br";

}
