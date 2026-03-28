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

import org.springframework.stereotype.Service;

import br.jus.tjce.releasecontroller.Parametros;
import br.jus.tjce.releasecontroller.redmine.dom.CustomField;
import br.jus.tjce.releasecontroller.redmine.dom.Issue;
import br.jus.tjce.releasecontroller.redmine.dom.IssueChild;
import br.jus.tjce.releasecontroller.redmine.dom.User;
import br.jus.tjce.releasecontroller.redmine.dom.Project;
import br.jus.tjce.releasecontroller.redmine.dom.Tracker;
import br.jus.tjce.releasecontroller.redmine.dom.Status;
import br.jus.tjce.releasecontroller.redmine.dom.ProjectsResponse;
import br.jus.tjce.releasecontroller.redmine.dom.TrackersResponse;
import br.jus.tjce.releasecontroller.redmine.dom.StatusesResponse;
import br.jus.tjce.releasecontroller.util.StringUtil;

@Service
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
		return consumirEndPointRedmine(urlString, classe, true);
	}

	private <T> Optional<T> consumirEndPointRedmine(String urlString, Class<T> classe, boolean unwrapRoot) throws IOException {
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
			if (unwrapRoot) {
				mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
			} else {
				mapper.disable(DeserializationFeature.UNWRAP_ROOT_VALUE);
			}
			T objeto = mapper.readValue(sb.toString(), classe);
			return Optional.ofNullable(objeto);
		} finally {
			conn.disconnect();
		}
	}

	public List<Issue> buscarTarefas(Integer projetoId, Integer tipoId, Integer statusId, String dataCriacaoInicio, String dataCriacaoFim) {
		List<Issue> todasTarefas = new ArrayList<>();
		try {
			StringBuilder baseBuilder = new StringBuilder(Parametros.URL_BASE);
			baseBuilder.append("/issues.json?include=relations&limit=100");
			
			if (statusId != null) {
				baseBuilder.append("&status_id=").append(statusId);
			} else {
				baseBuilder.append("&status_id=*");
			}
			
			if (projetoId != null) {
				baseBuilder.append("&project_id=").append(projetoId);
			}
			if (tipoId != null) {
				baseBuilder.append("&tracker_id=").append(tipoId);
			}
			if (dataCriacaoInicio != null && dataCriacaoFim != null) {
				baseBuilder.append("&created_on=%3E%3C").append(dataCriacaoInicio).append("|").append(dataCriacaoFim);
			} else if (dataCriacaoInicio != null) {
				baseBuilder.append("&created_on=%3E%3D").append(dataCriacaoInicio);
			} else if (dataCriacaoFim != null) {
				baseBuilder.append("&created_on=%3C%3D").append(dataCriacaoFim);
			}

			int offset = 0;
			int limit = 100;
			boolean hasMore = true;

			while (hasMore) {
				String urlStr = baseBuilder.toString() + "&offset=" + offset;
				Optional<br.jus.tjce.releasecontroller.redmine.dom.IssuesResponse> response = consumirEndPointRedmine(urlStr, br.jus.tjce.releasecontroller.redmine.dom.IssuesResponse.class, false);
				
				if (response.isPresent() && response.get().getIssues() != null && !response.get().getIssues().isEmpty()) {
					List<Issue> rec = response.get().getIssues();
					todasTarefas.addAll(rec);
					
					if (rec.size() < limit) {
						hasMore = false;
					} else {
						offset += limit;
					}
				} else {
					hasMore = false;
				}
			}
		} catch (IOException e) {
			System.out.println("Erro ao buscar lista de tarefas");
			e.printStackTrace();
		}
		return todasTarefas;
	}

	public List<Project> buscarProjetos() {
		try {
			String urlStr = Parametros.URL_BASE + "/projects.json?limit=10000000";
			Optional<ProjectsResponse> response = consumirEndPointRedmine(urlStr, ProjectsResponse.class, false);
			if (response.isPresent() && response.get().getProjects() != null) {
				return response.get().getProjects();
			}
		} catch (IOException e) {
			System.out.println("Erro ao buscar lista de projetos: " + e.getMessage());
		}
		return new ArrayList<>();
	}

	public List<Tracker> buscarTipos() {
		try {
			String urlStr = Parametros.URL_BASE + "/trackers.json";
			Optional<TrackersResponse> response = consumirEndPointRedmine(urlStr, TrackersResponse.class, false);
			if (response.isPresent() && response.get().getTrackers() != null) {
				return response.get().getTrackers();
			}
		} catch (IOException e) {
			System.out.println("Erro ao buscar lista de tipos (trackers): " + e.getMessage());
		}
		return new ArrayList<>();
	}

	public List<Status> buscarStatuses() {
		try {
			String urlStr = Parametros.URL_BASE + "/issue_statuses.json";
			Optional<StatusesResponse> response = consumirEndPointRedmine(urlStr, StatusesResponse.class, false);
			if (response.isPresent() && response.get().getIssueStatuses() != null) {
				return response.get().getIssueStatuses();
			}
		} catch (IOException e) {
			System.out.println("Erro ao buscar lista de statuses: " + e.getMessage());
		}
		return new ArrayList<>();
	}

	private String obterValorCampoCustomizado(Issue tarefa, String nomeCampo) {
		if (tarefa.getCustomFields() != null) {
			for (CustomField field : tarefa.getCustomFields()) {
				if (field.getName() != null && field.getName().contains(nomeCampo) && field.getValue() != null) {
					return field.getValue().toString();
				}
			}
		}
		return "";
	}

	public void imprimirBuscarTarefas(Integer projetoId, Integer tipoId, Integer statusId, String dataCriacaoInicio, String dataCriacaoFim) {
		List<Issue> tarefas = buscarTarefas(projetoId, tipoId, statusId, dataCriacaoInicio, dataCriacaoFim);
		for (Issue tarefa : tarefas) {
			try {
				String linha = gerarLinha(tarefa, 0);
				if (linha != null) {
					System.out.println(linha);
				}
			} catch (Exception e) {
				System.out.println("Erro ao imprimir tarefa #" + tarefa.getId());
			}
		}
	}

	public void exportarTarefasParaCSV(Integer projetoId, Integer tipoId, Integer statusId, String dataCriacaoInicio, String dataCriacaoFim, String caminhoArquivo) {
		List<Issue> tarefas = buscarTarefas(projetoId, tipoId, statusId, dataCriacaoInicio, dataCriacaoFim);
		try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(caminhoArquivo))) {
			writer.println("ID;Tarefa Relacionada;Tipo_demanda_relacionada;Status_relacionada;Projeto;Tipo_demanda;Status;Assunto;Autor;Data de Criação;[DEVPJE] - Quantidade;[DEVPJE] - Atividade Catálogo;[DEVPJE] Complexidade");
			for (Issue tarefa : tarefas) {
				String id = tarefa.getId() != null ? tarefa.getId().toString() : "";
				
				String tarefaRelacionada = "";
				String tipoDemandaRelacionada = "";
				String statusRelacionada = "";
				if (tarefa.getRelations() != null && !tarefa.getRelations().isEmpty()) {
					br.jus.tjce.releasecontroller.redmine.dom.Relation rel = tarefa.getRelations().get(0);
					Integer relId = tarefa.getId().equals(rel.getIssueId()) ? rel.getIssueToId() : rel.getIssueId();
					tarefaRelacionada = relId != null ? relId.toString() : "";
					if (relId != null) {
						Optional<Issue> related = obterTarefa(relId);
						if (related.isPresent() && related.get().getTracker() != null) {
							tipoDemandaRelacionada = related.get().getTracker().getName();
						}
						if (related.isPresent() && related.get().getStatus() != null) {
							statusRelacionada = related.get().getStatus().getName();
						}
					}
				}
				
				String projeto = tarefa.getProject() != null ? tarefa.getProject().getName() : "";
				String tracker = tarefa.getTracker() != null ? tarefa.getTracker().getName() : "";
				String status = tarefa.getStatus() != null ? tarefa.getStatus().getName() : "";
				String assunto = tarefa.getSubject() != null ? tarefa.getSubject().replace(";", ",") : "";
				String autor = tarefa.getAuthor() != null ? tarefa.getAuthor().getName() : "";
				String dataCriacao = tarefa.getCreatedOn() != null ? tarefa.getCreatedOn().toString() : "";
				String qte = obterValorCampoCustomizado(tarefa, "Quantidade");
				String atividade = obterValorCampoCustomizado(tarefa, "Atividade Catálogo");
				String complexidade = obterValorCampoCustomizado(tarefa, "Complexidade");
				
				writer.println(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s", id, tarefaRelacionada, tipoDemandaRelacionada,statusRelacionada, projeto, tracker, status, assunto,autor, dataCriacao, qte, atividade, complexidade));
			}
			System.out.println("Arquivo CSV gerado com sucesso em: " + caminhoArquivo);
		} catch (IOException e) {
			System.out.println("Erro ao gerar arquivo CSV: " + e.getMessage());
			e.printStackTrace();
		}
	}
	public byte[] exportarTarefasParaCSVBytes(Integer projetoId, Integer tipoId, Integer statusId, String dataCriacaoInicio, String dataCriacaoFim) {
		List<Issue> tarefas = buscarTarefas(projetoId, tipoId, statusId, dataCriacaoInicio, dataCriacaoFim);
		try (java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
			 java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.OutputStreamWriter(out, java.nio.charset.StandardCharsets.UTF_8))) {
			
			writer.println('\uFEFF' + "ID;Tarefa Relacionada;Tipo_demanda_relacionada;Projeto;Tipo_demanda;Status;Assunto;Data de Criação;[DEVPJE] - Quantidade;[DEVPJE] - Atividade Catálogo;[DEVPJE] Complexidade"); // BOM for excel
			for (Issue tarefa : tarefas) {
				String id = tarefa.getId() != null ? tarefa.getId().toString() : "";
				
				String tarefaRelacionada = "";
				String tipoDemandaRelacionada = "";
				if (tarefa.getRelations() != null && !tarefa.getRelations().isEmpty()) {
					br.jus.tjce.releasecontroller.redmine.dom.Relation rel = tarefa.getRelations().get(0);
					Integer relId = tarefa.getId().equals(rel.getIssueId()) ? rel.getIssueToId() : rel.getIssueId();
					tarefaRelacionada = relId != null ? relId.toString() : "";
					if (relId != null) {
						Optional<Issue> related = obterTarefa(relId);
						if (related.isPresent() && related.get().getTracker() != null) {
							tipoDemandaRelacionada = related.get().getTracker().getName();
						}
					}
				}
				
				String projeto = tarefa.getProject() != null ? tarefa.getProject().getName() : "";
				String tracker = tarefa.getTracker() != null ? tarefa.getTracker().getName() : "";
				String status = tarefa.getStatus() != null ? tarefa.getStatus().getName() : "";
				String assunto = tarefa.getSubject() != null ? tarefa.getSubject().replace(";", ",") : "";
				String dataCriacao = tarefa.getCreatedOn() != null ? tarefa.getCreatedOn().toString() : "";
				String qte = obterValorCampoCustomizado(tarefa, "Quantidade");
				String atividade = obterValorCampoCustomizado(tarefa, "Atividade Catálogo");
				String complexidade = obterValorCampoCustomizado(tarefa, "Complexidade");
				
				writer.println(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s;%s", id, tarefaRelacionada, tipoDemandaRelacionada, projeto, tracker, status, assunto, dataCriacao, qte, atividade, complexidade));
			}
			writer.flush();
			return out.toByteArray();
		} catch (Exception e) {
			System.out.println("Erro ao gerar arquivo CSV: " + e.getMessage());
			e.printStackTrace();
			return new byte[0];
		}
	}

}
