# CheckInLocais — INF311 Prática 05

Projeto Android nativo em Java para a atividade prática 05: aplicativo de check-in em locais visitados, com persistência local em SQLite, localização do usuário e visualização dos locais em mapa.

## Requisitos atendidos

- API level mínima 21.
- 4 telas: Tela Principal, Mapa de Check-in, Gestão de Check-in e Relatório de Locais Mais Visitados.
- Banco SQLite com as tabelas `Categoria` e `Checkin` no formato solicitado.
- Categorias padrão cadastradas automaticamente: Restaurante, Bar, Cinema, Universidade, Estádio, Parque e Outros.
- Campo de local usando `AutoCompleteTextView` com locais já visitados.
- Categoria usando `Spinner`, carregado em ordem crescente de `idCategoria`.
- Captura de latitude e longitude via Fused Location Provider.
- Inserção de novo check-in com `qtdVisitas = 1`.
- Atualização de check-in existente incrementando `qtdVisitas` e desconsiderando categoria/localização novas.
- Menu da tela principal com acesso a mapa, gestão e relatório.
- Mapa com marcadores contendo `title` e `snippet` com categoria e quantidade de visitas.
- Menu do mapa com voltar, gestão, relatório e submenu para mapa normal/híbrido.
- Gestão com lista dinâmica de locais e botões de exclusão com confirmação.
- Relatório ordenado por quantidade de visitas em ordem decrescente.

## Como executar

1. Abra a pasta `CheckInLocais` no Android Studio.
2. Aguarde o Gradle sincronizar o projeto.
3. No arquivo `gradle.properties`, substitua:

```properties
MAPS_API_KEY=INSIRA_SUA_CHAVE_AQUI
```

pela sua chave da API do Google Maps.

4. Execute em um emulador ou dispositivo físico com Google Play Services.
5. Conceda a permissão de localização quando solicitada.

## Observações importantes

- Para o mapa funcionar, a API **Maps SDK for Android** precisa estar habilitada no projeto do Google Cloud usado na chave.
- Para a localização funcionar no emulador, defina uma posição simulada no painel do Android Emulator.
- O projeto foi preparado como código-fonte para importação no Android Studio. O APK compilado não foi incluído.

## Estrutura principal

```text
app/src/main/java/br/ufv/inf311/checkinlocais/
├── MainActivity.java
├── MapaCheckinActivity.java
├── GestaoActivity.java
├── RelatorioActivity.java
├── data/DatabaseHelper.java
└── model/
    ├── Categoria.java
    └── Checkin.java
```
