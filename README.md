# Redmine Task Exporter

Este projeto é um utilitário desenvolvido em Java com o framework **Spring Boot** para se conectar e interagir com a API do sistema **Redmine** do TJCE. Seu principal objetivo é fornecer uma interface web amigável para pesquisar tarefas (issues) e exportá-las em formato CSV com informações chaves formatadas, como a tarefa pai relacionada, campos customizados e outros.

## Tecnologias Utilizadas

- **Java 17+**
- **Spring Boot (Spring Web)**
- **Thymeleaf**: Para renderização de templates HTML dinamicamente no lado do servidor.
- **Jackson (fasterxml)**: Para realizar a serialização/desserialização das respostas das APIs JSON do Redmine.
- **HTML/CSS Vanilla**: Para a interface visual do sistema.

## Funcionalidades
- Pesquisa de Projetos, Tipos (Trackers) e Status diretamente através da API do Redmine.
- Busca por período de Datas de Criação.
- Exportação dinâmica de CSV com o separador `;` pronto para ser aberto perfeitamente no Excel, incluindo proteção contra codificações de texto (BOM UTF-8).
- Extração de múltiplos campos customizados, incluindo:
  - `[DEVPJE] - Quantidade`
  - `[DEVPJE] - Atividade Catálogo`
  - `[DEVPJE] Complexidade`

## Como Executar

Por ser um projeto Maven/Spring Boot padrão, existem algumas maneiras de executá-lo a partir do seu terminal no diretório raiz do projeto.

### Execução Normal (Modo de Produção/Desenvolvimento)

Para limpar o projeto e iniciar o servidor embutido do Spring (geralmente na porta 8080):
```bash
mvn clean spring-boot:run
```

Acesse a interface da aplicação pelo navegador em: `http://localhost:8080` (A porta pode variar dependendo do que está configurado no seu `application.properties`).

### Como Executar em Modo Debug

Para executar a aplicação possibilitando o uso de pontos de parada (breakpoints) através de uma IDE (Eclipse, IntelliJ, VS Code):

**Opção 1: Usando `mvnDebug`**
```bash
mvnDebug clean spring-boot:run
```
Ao executar este comando, a JVM ficará em um estado de espera, aguardando que você conecte o seu Debugger remotamente na porta **8000**.

**Opção 2: Passando Agentes da JVM Diretamente**
Caso queira depurar usando uma porta diferente (por exemplo, 5005) ou sem suspender a inicialização imediatamente:
```bash
mvn clean spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```
Assim que a aplicação ligar, conecte o Debug Remoto de sua IDE na porta **5005**.
