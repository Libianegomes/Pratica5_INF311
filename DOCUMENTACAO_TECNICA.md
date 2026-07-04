# Documentação Técnica — CheckInLocais

## 1. Banco de dados

O banco local é criado pela classe `DatabaseHelper`, usando `SQLiteOpenHelper`.

### Tabela `Categoria`

```sql
CREATE TABLE Categoria (
    idCategoria INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL
);
```

Categorias inseridas automaticamente:

1. Restaurante
2. Bar
3. Cinema
4. Universidade
5. Estádio
6. Parque
7. Outros

### Tabela `Checkin`

```sql
CREATE TABLE Checkin (
    Local TEXT PRIMARY KEY,
    qtdVisitas INTEGER NOT NULL,
    cat INTEGER NOT NULL,
    latitude TEXT NOT NULL,
    longitude TEXT NOT NULL,
    CONSTRAINT fkey0 FOREIGN KEY (cat) REFERENCES Categoria (idCategoria)
);
```

## 2. Tela Principal

Classe: `MainActivity`

Responsabilidades:

- Carregar categorias no `Spinner` em ordem crescente de chave primária.
- Carregar locais já visitados no `AutoCompleteTextView`.
- Capturar latitude e longitude em tempo real.
- Validar se o usuário informou local, categoria e se há localização disponível.
- Inserir novo check-in quando o local não existe.
- Incrementar `qtdVisitas` quando o local já existe.
- Reiniciar a tela após inserção ou atualização.
- Disponibilizar menu para Mapa, Gestão e Relatório.

## 3. Mapa de Check-in

Classe: `MapaCheckinActivity`

Responsabilidades:

- Receber latitude e longitude atuais por parâmetro da tela principal.
- Centralizar o mapa na posição atual do usuário.
- Buscar todos os check-ins cadastrados no SQLite.
- Criar um marcador para cada check-in.
- Usar o nome do local como `title`.
- Usar categoria e quantidade de visitas como `snippet`.
- Permitir alternância entre mapa normal e híbrido.

## 4. Gestão de Check-in

Classe: `GestaoActivity`

Responsabilidades:

- Listar dinamicamente todos os locais cadastrados.
- Criar um `ImageButton` para cada local.
- Usar a `tag` do botão para identificar o local a ser excluído.
- Solicitar confirmação antes de excluir.
- Reiniciar a tela após exclusão.

## 5. Relatório de Locais Mais Visitados

Classe: `RelatorioActivity`

Responsabilidades:

- Listar todos os locais visitados.
- Exibir a quantidade de visitas de cada local.
- Ordenar o resultado por `qtdVisitas DESC`.

## 6. Permissões

O app declara as permissões:

```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

A permissão de localização é solicitada em tempo de execução.
