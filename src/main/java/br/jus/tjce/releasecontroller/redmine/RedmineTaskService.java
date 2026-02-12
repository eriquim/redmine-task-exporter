package br.jus.tjce.releasecontroller.redmine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.jus.tjce.releasecontroller.Parametros;
import br.jus.tjce.releasecontroller.redmine.dom.CustomField;
import br.jus.tjce.releasecontroller.redmine.dom.Issue;
import br.jus.tjce.releasecontroller.redmine.dom.IssueChild;
import br.jus.tjce.releasecontroller.redmine.dom.User;
import br.jus.tjce.releasecontroller.util.StringUtil;

public class RedmineTaskService {

	public void imprimirTarefasApartirDoPai() {
		try {
			Optional<Issue> noTarefaPai = obterTarefa(Parametros.ISSUE_PAI);
			if (noTarefaPai.isPresent()) {
				imprimirTarefaById(noTarefaPai.get().getId(), 0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String gerarLinha(Issue tarefa, int nivel) throws Exception {
		try {
			List<String> responsaveis = new ArrayList<>();
			String tipoAtividade = "(Não definido)";
			Iterator<CustomField> customFieldsIterator = tarefa.getCustomFields().iterator();
			while (customFieldsIterator.hasNext()) {
				CustomField customField = customFieldsIterator.next();
				if (customField.getName().equalsIgnoreCase("Responsáveis (Execução)")) {
					if (customField.isMultiple()) {
						List<String> idsResponsaveis =  (ArrayList) customField.getValue();
						if (idsResponsaveis != null && !idsResponsaveis.isEmpty()) {
							for (String idResponsavel : idsResponsaveis) {
								Optional<User> responsavel = obterUsuarioPorId(Integer.valueOf(idResponsavel));
								if (responsavel.isPresent()) {
									responsaveis.add(responsavel.get().getFirstname());
								}
							}
						}
					}
				}
				if (customField.getName().equalsIgnoreCase("[DEVPJE] Tipo Atividade")) {
					tipoAtividade = (String) customField.getValue();
					if (tipoAtividade == null || tipoAtividade.isEmpty()) {
						tipoAtividade = "(Não definido)";
					}
				}
			}
			if (tarefa.getAssignedTo() != null && (responsaveis == null || responsaveis.isEmpty())
					&& (tarefa.getTracker().getName().equals("Configurar PJE")
							|| tarefa.getTracker().getName().equals("Serviço de Apoio PJE"))) {
				Optional<User> atribuidoPara = obterUsuarioPorId(tarefa.getAssignedTo().getId());
				tipoAtividade = atribuidoPara.isPresent() ? atribuidoPara.get().getFirstname()
						: "(Não encontrado id: " + tarefa.getAssignedTo().getId() + ")";
			}

//			if ( 
//					  tarefa.getTracker().getName().equals("Desenvolvimento PJE") ||
//					  tarefa.getTracker().getName().equals("Demanda") ||
//					  tarefa.getTracker().getName().equals("Configurar PJE") ||
//					  tarefa.getTracker().getName().equals("Serviço de Apoio PJE") &&
//				 !tarefa.getTracker().getName().equals("Entrega") && !tarefa.getStatus().getName().equals("Fechada")) {
			String linha = null;
			if (responsaveis != null && !responsaveis.isEmpty()) {
				String nomesResponsaveis = StringUtil.tratarNomes(responsaveis);
				if (Parametros.IMPRIMIR_CONSOLE) {
					linha = StringUtil.gerarEspacos(nivel) + "+ [" + tipoAtividade + "/" + nomesResponsaveis + "] "
							+ tarefa.getTracker().getName() + " #" + tarefa.getId() + " - " + tarefa.getSubject()
							+ " (" + tarefa.getStatus().getName() + ")";
				}
			} else {
				if (Parametros.IMPRIMIR_CONSOLE) {
					linha = StringUtil.gerarEspacos(nivel) + "+ [" + tipoAtividade + "] "
							+ tarefa.getTracker().getName() + " #" + tarefa.getId() + " - " + tarefa.getSubject()
							+ " (" + tarefa.getStatus().getName() + ")";
				}
			}
			return linha;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void imprimirTarefaById(Integer idTarefa, int nivel) throws Exception {
		Optional<Issue> tarefa = obterTarefa(idTarefa);
		if(tarefa.isPresent()) {
			String linha = gerarLinha(tarefa.get(), nivel);
			if(linha != null) {
				System.out.println(linha);
			}
		}
		if (tarefa.isPresent() && tarefa.get().getChildren() != null) {
			++nivel;
			for (IssueChild child : tarefa.get().getChildren()) {
				imprimirTarefaById(child.getId(), nivel);
			}
		}
	}

	public Optional<User> obterUsuarioPorId(int userId) throws Exception {
		String urlStr = String.format("%s/users/%d.json?include=memberships,groups", Parametros.URL_BASE, userId);
		try {
			Optional<User> user = consumirEndPointRedmine(urlStr, User.class);
			return user;
		} catch (IOException e) {
			System.out.println("Erro ao buscar o usuário atual");
			return null;
		}
	}

	public Optional<User> obterUsuarioAtual() throws Exception {
		String urlStr = String.format("%s/users/current.json", Parametros.URL_BASE);
		try {
			Optional<User> user = consumirEndPointRedmine(urlStr, User.class);
			return user;
		} catch (IOException e) {
			System.out.println("Erro ao buscar o usuário atual");
			return null;
		}
	}

	public Optional<Issue> obterTarefa(Integer idTarefa) {
		try {
			String urlStr = "https://redmine.tjce.jus.br/issues/" + idTarefa + ".json?include=children";
			Optional<Issue> issue = consumirEndPointRedmine(urlStr, Issue.class);
			return issue;
		} catch (IOException e) {
			System.out.println("Erro ao buscar subtarefa ID " + idTarefa);
			e.printStackTrace();
			return null;
		}
	}

	private <T> Optional<T> consumirEndPointRedmine(String urlString, Class<T> classe) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("X-Redmine-API-Key", Parametros.API_KEY);
		conn.setRequestProperty("Accept", "application/json");

		int status = conn.getResponseCode();
		if (status != 200) {
			System.err.println("Erro ao consultar o Redmine. HTTP status: " + status);
			conn.disconnect();
			return Optional.empty();
		}

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
			T objeto = mapper.readValue(sb.toString(), classe);
			return Optional.ofNullable(objeto);
		} finally {
			conn.disconnect();
		}
	}

}
