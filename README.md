[README_Heal_Healthcare.md](https://github.com/user-attachments/files/33616722/README_Heal_Healthcare.md)
<div align="center">

# 🏥 Heal Healthcare
### Sistema de Gestão Clínica

Aplicação desktop para apoiar a rotina de uma clínica médica, reunindo o cadastro de pacientes e profissionais, o agendamento de consultas e o registro de informações clínicas.

![Java](https://img.shields.io/badge/Java-21-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-25-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-green)
![Maven](https://img.shields.io/badge/Maven-Build-red)
![Docker](https://img.shields.io/badge/Docker-MySQL-2496ED?logo=docker&logoColor=white)
![Launch4j](https://img.shields.io/badge/Launch4j-Windows%20EXE-6C5CE7)
![Proxmox](https://img.shields.io/badge/Testing-Proxmox%20%2B%20Windows-E57000)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow)

</div>

---

## 📑 Sumário

- [Sobre o projeto](#-sobre-o-projeto)
- [Funcionalidades](#-funcionalidades)
- [Tecnologias utilizadas](#-tecnologias-utilizadas)
- [Modelagem e banco de dados](#-modelagem-e-banco-de-dados)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Como executar](#-como-executar)
- [Usuários para teste](#-usuários-para-teste)
- [Contexto acadêmico](#-contexto-acadêmico)
- [Equipe](#-equipe)
- [Licença e uso](#-licença-e-uso)

## 💡 Sobre o projeto

O **Heal Healthcare** é um sistema desktop desenvolvido em Java para apoiar a gestão de clínicas médicas. A aplicação reúne funcionalidades administrativas e clínicas em uma interface gráfica construída com JavaFX, com persistência de dados em um banco de dados MySQL.

O sistema contempla o gerenciamento de pacientes, médicos e especialidades, além do fluxo de agendamento de consultas e do registro de informações em prontuários.

O projeto foi desenvolvido como parte da **Avaliação Integrada A3** da unidade curricular **Modelagem de Software (código 0004824)**, no período letivo **2026/2**, na Universidade São Judas Tadeu (USJT).

## ✨ Funcionalidades

### 🔐 Autenticação e controle de acesso
- Login de usuários.
- Diferenciação de perfis: administrador, recepção e médico.
- Senhas com hash utilizando BCrypt, conforme a implementação descrita no projeto.

### 🧑‍🤝‍🧑 Gestão de pacientes
- Cadastro, consulta, edição e exclusão de pacientes (CRUD).
- Registro de responsável legal para pacientes menores de idade.

### 🩺 Gestão de médicos e especialidades
- Cadastro e gerenciamento de médicos.
- Associação de médicos às respectivas especialidades.
- Registro do número de CRM.

### 📅 Agenda médica
- Agendamento, confirmação e cancelamento de consultas.
- Validação de conflitos de horário.

### 📋 Prontuário eletrônico
- Área destinada ao médico responsável.
- Registro de observações clínicas e prescrições após a consulta.

> **Observação:** as permissões e os fluxos disponíveis dependem do perfil autenticado e da implementação atual do sistema.

## 🧰 Tecnologias utilizadas

| Tecnologia | Aplicação no projeto |
|---|---|
| **Java 21** | Linguagem principal |
| **JavaFX 25** | Construção da interface desktop |
| **FXML / Scene Builder** | Estruturação das telas |
| **MySQL 8.0** | Armazenamento relacional dos dados |
| **Docker** | Execução do MySQL em contêiner |
| **Maven** | Gerenciamento da construção e das dependências |
| **Launch4j** | Geração de um executável `.exe` para Windows |
| **Proxmox VE** | Ambiente virtualizado para testes |
| **Windows** | Sistema operacional utilizado nos testes de resolução |
| **MySQL Workbench** | Administração e modelagem do banco de dados |
| **diagrams.net (draw.io)** | Criação dos diagramas UML |

## 🧪 Ambiente de testes e distribuição

Além do desenvolvimento da aplicação, foram utilizadas ferramentas para validar o comportamento da interface e preparar a execução no Windows:

- **Docker:** utilizado para executar o MySQL em contêiner, facilitando a configuração do banco de dados no ambiente de desenvolvimento.
- **Proxmox VE + Windows:** ambiente virtualizado utilizado para testar a aplicação em diferentes resoluções de tela e observar o comportamento da interface.
- **Launch4j:** utilizado para empacotar a aplicação Java em um executável `.exe` para Windows.

Essas ferramentas complementam o desenvolvimento, permitindo testar a interface em condições distintas e facilitar a inicialização da aplicação no ambiente Windows.

## 🗃️ Modelagem e banco de dados

O sistema utiliza o banco de dados **`clinica_medica`**. O modelo foi projetado com o objetivo de organizar os dados relacionais e reduzir redundâncias, seguindo a proposta de normalização até a **Terceira Forma Normal (3FN)**.

Principais entidades:

- `usuarios` — dados de autenticação e perfil de acesso.
- `pacientes` — cadastro de pacientes e informações relacionadas.
- `medicos` — dados dos profissionais de saúde.
- `especialidades` — especialidades médicas.
- `consultas` — informações de agendamento.
- `prontuarios` — registros clínicos associados às consultas/pacientes, conforme o modelo implementado.

O script SQL de criação e carga inicial está em:

```text
SQL-Files/SQL.sql
```

## 📁 Estrutura do projeto

Visão resumida dos diretórios e arquivos relevantes:

```text
Heal-Healthcare/
├── SQL-Files/
│   └── SQL.sql
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/sistema_gestao_pacientes/
│   │   │       ├── Agenda/
│   │   │       ├── Config/
│   │   │       ├── Dashboard/
│   │   │       ├── Login/
│   │   │       ├── Medicos/
│   │   │       ├── Pacientes/
│   │   │       ├── Prontuario/
│   │   │       ├── Util/
│   │   │       ├── Main.java
│   │   │       ├── MainApp.java
│   │   │       └── Launcher.java
│   │   └── resources/
│   │       ├── *.fxml
│   │       ├── style.css
│   │       ├── database.properties
│   │       └── imagens/
│   └── test/
├── pom.xml
├── mvnw
└── mvnw.cmd
```

**Organização geral:**
- `Agenda/`, `Medicos/`, `Pacientes/` e `Prontuario/`: controladores e funcionalidades dos módulos.
- `Config/`: configuração e conexão com o banco de dados.
- `Login/`: autenticação.
- `Util/`: classes auxiliares, sessões e objetos utilizados pela aplicação.
- `src/main/resources/`: telas FXML, estilos, configurações e imagens.
- `SQL-Files/`: script SQL do banco de dados.

> Arquivos gerados em `target/` e configurações locais da IDE não são necessários para documentar a estrutura principal do projeto.

## 🚀 Como executar

### Pré-requisitos

Antes de iniciar, instale ou configure:

- **JDK 17 ou superior**.
- **MySQL Server**.
- **Maven** (opcional se utilizar o Maven Wrapper incluído no repositório).
- Uma IDE compatível com Java, como IntelliJ IDEA, Eclipse ou VS Code com as extensões necessárias.

### 1. Obter o projeto

Clone o repositório e entre na pasta:

```bash
git clone <URL_DO_REPOSITORIO>
cd <PASTA_DO_REPOSITORIO>
```

Substitua os valores entre `< >` pela URL e pelo nome real do repositório.

### 2. Criar o banco de dados

1. Inicie o MySQL Server.
2. Abra o MySQL Workbench ou outro cliente MySQL.
3. Execute o script `SQL-Files/SQL.sql`.
4. Confirme que o banco `clinica_medica` e as tabelas foram criados.

### 3. Configurar a conexão

Localize a classe:

```text
src/main/java/com/example/sistema_gestao_pacientes/Config/DatabaseConnection.java
```

Configure o endereço do servidor, a porta, o nome do banco, o usuário e a senha de acordo com o ambiente local. A configuração atual do projeto pode exigir alteração das credenciais diretamente nessa classe.

**Segurança:** não publique senhas reais no repositório. Para uma versão de produção, utilize variáveis de ambiente ou um arquivo de configuração local fora do controle de versão.

### 4. Executar a aplicação

Na raiz do projeto, use o Maven Wrapper.

**Windows (PowerShell ou Prompt de Comando):**

```bat
mvnw.cmd clean javafx:run
```

**Linux / macOS:**

```bash
chmod +x mvnw
./mvnw clean javafx:run
```

Se a execução pelo wrapper não estiver configurada no `pom.xml`, abra o projeto na IDE, aguarde o carregamento das dependências Maven e execute a classe de inicialização configurada no projeto (`Main.java`, `MainApp.java` ou `Launcher.java`, conforme a configuração da aplicação).

## 🧪 Usuários para teste

O material do projeto informa os seguintes usuários de demonstração:

| Perfil | Usuário | Senha |
|---|---|---|
| Administrador | `admin` | `123` |
| Médico | `dr.roberto` | `123` |

> **Importante:** essas credenciais são apenas para testes acadêmicos, caso ainda estejam cadastradas no banco inicial. Altere-as antes de qualquer uso real. Não utilize essas senhas em ambientes públicos ou de produção.

## 🎓 Contexto acadêmico

**Instituição:** Universidade São Judas Tadeu (USJT)  
**Unidade curricular:** Modelagem de Software  
**Avaliação:** Avaliação Integrada A3  
**Código da unidade curricular:** 0004824  
**Período letivo:** 2026/2  
**Professor responsável:** Claudio Boghi

### Entregas e objetivos do projeto

- Levantamento e especificação de requisitos funcionais (RF), não funcionais (RNF) e regras de negócio (RN).
- Modelagem UML: casos de uso, classes, sequência, atividades e transição de estados.
- Modelagem de banco de dados relacional, com proposta de normalização até a 3FN.
- Implementação de operações CRUD conectadas ao banco de dados.

## 👥 Equipe

| Integrante | RA |
|---|---:|
| Breno Ramos dos Santos | 823159781 |
| Guilherme dos Santos Coelho | 823120490 |
| Guilherme Feitosa Santana | 823111787 |
| Victor Henrique da Silva Perillo Brasil | 823130525 |

## 📄 Licença e uso

Projeto acadêmico desenvolvido em 2026 na Universidade São Judas Tadeu. O material destina-se a fins educacionais; não representa, por si só, um sistema clínico certificado para uso profissional.

Antes de utilizar ou distribuir o software fora do contexto acadêmico, revise a segurança, o controle de acesso, a proteção de dados pessoais e os requisitos legais aplicáveis, especialmente por envolver informações de saúde.
