package br.jus.tjce.releasecontroller.git;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.jus.tjce.releasecontroller.Parametros;

public class GitLabMRDownloader {
 

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        String sourceBranch = getSourceBranch();
        JsonNode changes = getMRChanges();
        boolean baixar = false;
        String conteudoArquivoNovosFluxos = null; // new String(Files.readAllBytes(Paths.get(caminho)));
        
        FileWriter writerFluxos = new FileWriter("fluxos_versao.txt");
        writerFluxos.write(" ---- Todos Fluxos da releases ---- " + System.lineSeparator());
        
        FileWriter writerFluxosExistentes = new FileWriter("fluxos_existentes_versao.txt");
        writerFluxosExistentes.write(" ---- Fluxos existentes da releases ---- " + System.lineSeparator());
       
       
        
        FileWriter writerScripts = new FileWriter("scripts_versao.txt");
        writerScripts.write(" ---- Scripts da releases ---- " + System.lineSeparator());
        
        
        int iFluxos = 0;
        int iFluxosExistente = 0;
        int iScripts= 0;

        for (JsonNode change : changes) {
            String filePath = change.get("new_path").asText();
            System.out.println("Baixando: " + filePath);
            
            //Fluxos
            if (filePath.contains(".xml")) {
            	if(baixar) {
            		byte[] content = downloadFile(filePath, sourceBranch);
            		
            		Path localFile = Paths.get(filePath);
            		if (localFile.getParent() != null) {
            			Files.createDirectories(localFile.getParent());
            		}
            		Files.write(localFile, content);
            	}
            	++iFluxos;
            	
            	String nomeArquivo = filePath;
//            	.replace("Fluxos/1o Grau/Criminal/","");
//            	nomeArquivo = nomeArquivo.replace("Fluxos/1o Grau/ECarta/","");
//            	nomeArquivo = nomeArquivo.replace("Fluxos/1o Grau/Geral/","");
//            	nomeArquivo = nomeArquivo.replace("Fluxos/1o Grau/Infância e Juventude/","");
            	
            	writerFluxos.write(iFluxos+"- " + nomeArquivo + System.lineSeparator());
            
            	if(conteudoArquivoNovosFluxos!= null && !conteudoArquivoNovosFluxos.contains(nomeArquivo.replace(".xml", ""))) {
            		iFluxosExistente++;
            		writerFluxosExistentes.write(iFluxosExistente+"- " + nomeArquivo + System.lineSeparator());
            	}
            	
            }
            
            //Scripts
            if (//!filePath.contains("C083.F005") && 
            		filePath.contains(".sql")) {
            	if(baixar) {
		            byte[] content = downloadFile(filePath, sourceBranch);
		
		            Path localFile = Paths.get(filePath);
		            if (localFile.getParent() != null) {
		                Files.createDirectories(localFile.getParent());
		            }
		            Files.write(localFile, content);
            	}
            	++iScripts;
            	
            	//String nomeArquivo = filePath.replace("Scripts/releases/C083.F005/DML/","");
            	String nomeArquivo = filePath.replace("Scripts/","");
            	//nomeArquivo = nomeArquivo.replace("Scripts/releases/C083.F005/Validação/","");
            	//nomeArquivo = nomeArquivo.replaceAll("^Scripts/\\d{4}/\\d{1,6}/", "");
            	       
            	writerScripts.write(iScripts+"- " + nomeArquivo + System.lineSeparator());
            }
        }
        writerFluxos.close();
        writerScripts.close();
        writerFluxosExistentes.close();
        System.out.println("Processamento concluído!");
    }

    private static String getSourceBranch() throws Exception {
        String url = Parametros.GITLAB_URL + "/projects/" + Parametros.PROJECT_ID + "/merge_requests/" + Parametros.MR_IID;
        JsonNode node = doGet(url);
        return node.get("source_branch").asText();
    }

    private static JsonNode getMRChanges() throws Exception {
        String url = Parametros.GITLAB_URL + "/projects/" + Parametros.PROJECT_ID + "/merge_requests/" + Parametros.MR_IID + "/changes";
        JsonNode node = doGet(url);
        return node.get("changes");
    }

    private static byte[] downloadFile(String filePath, String branch) throws Exception {
        String encodedPath = URLEncoder.encode(filePath, "UTF-8").replace("+", "%20");
        String url = Parametros.GITLAB_URL + "/projects/" + Parametros.PROJECT_ID + "/repository/files/" + encodedPath + "/raw?ref=" +
                URLEncoder.encode(branch, "UTF-8");
        return doGetBytes(url);
    }

    private static JsonNode doGet(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestProperty("PRIVATE-TOKEN", Parametros.PRIVATE_TOKEN);
        conn.setRequestMethod("GET");

        try (InputStream in = conn.getInputStream()) {
            return mapper.readTree(in);
        }
    }

    private static byte[] doGetBytes(String urlStr) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestProperty("PRIVATE-TOKEN", Parametros.PRIVATE_TOKEN);
        conn.setRequestMethod("GET");

        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] data = new byte[4096];
            int nRead;
            while ((nRead = in.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }
    
    public static Optional<JsonNode> compararBranches(
            String apiUrl,
            String privateToken,
            String projectId,
            String fromBranch,
            String toBranch
    ) throws IOException {

        String endpoint = String.format(
            "%s/projects/%s/repository/compare?from=%s&to=%s",
            apiUrl,
            projectId,
            fromBranch,
            toBranch
        );

        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("PRIVATE-TOKEN", privateToken);
        conn.setRequestProperty("Accept", "application/json");

        int status = conn.getResponseCode();
        InputStream is = (status >= 200 && status < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }

        conn.disconnect();

        if (status >= 200 && status < 300) {
            ObjectMapper mapper = new ObjectMapper();
            return Optional.of(mapper.readTree(sb.toString()));
        } else {
            System.err.println("Erro ao comparar branches (HTTP " + status + "): " + sb);
            return Optional.empty();
        }
    }
}
    
}

