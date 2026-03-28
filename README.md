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

## Pré-requisitos

Para executar o projeto localmente, você precisará apenas do **Java** instalado na sua máquina.

- [Java JDK ou JRE](https://www.oracle.com/java/technologies/downloads/) instalado e recém configurado nas variáveis de ambiente (`JAVA_HOME`).
- *Opcional:* Maven instalado na máquina, caso queira baixar as dependências e compilar a aplicação do zero.

---

## Como Configurar para Desenvolvimento

Caso você deseje continuar o desenvolvimento do sistema (adicionando novas rotas, alterando funcionalidades ou depurando), você deverá manipular o projeto com o Maven.

Para limpar o projeto, instalar as dependências e iniciar o servidor embutido do Spring (geralmente na porta 8080):
```bash
mvn clean spring-boot:run
```

Acesse a interface da aplicação pelo navegador em: `http://localhost:8080` (A porta pode variar dependendo do que está configurado no seu `application.properties`).

### Executando em Modo Debug

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

---

## Como Configurar para Execução (Apenas com o Java instalado)

Para rodar a aplicação em um ambiente de produção ou qualquer máquina de uso geral (apenas com o interpretador Java configurado), você pode utilizar a versão previamente compilada do projeto (um arquivo `.jar`), dispensando a necessidade de instalar ferramentas de compilação como o Maven neste ambiente.

1. Navegue até o diretório onde o arquivo `.jar` está guardado (caso você mesmo for compilar pelo Maven em sua máquina de desenvolvimento, gere o arquivo executando `mvn clean package` e acesse a pasta `target/`).
2. Abra o terminal (Prompt de Comando/PowerShell) nessa pasta.
3. Execute o programa usando o comando nativo do Java:
   ```bash
   java -jar redmine-task-exporter-0.0.1-SNAPSHOT.jar
   ```

A aplicação subirá o servidor embutido e estará disponível para ser acessada pelo navegador em `http://localhost:8080`.
