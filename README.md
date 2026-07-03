# CampusFix 🔧🏫

Aplicação móvel Android para registo e acompanhamento de pedidos/ocorrências num
campus académico. Trabalho prático de **Computação Móvel** (Engenharia
Informática, IPVC/ESTG — Época de Recurso 2025/2026).

## Tecnologias

- **Kotlin** + **Jetpack Compose** (Material 3)
- **Navigation Compose** — navegação entre ecrãs
- **MVVM** — ViewModel + StateFlow
- **Supabase** — autenticação (Auth), base de dados PostgreSQL (Postgrest) e
  armazenamento de fotografias (Storage)
- **Coil** — carregamento de imagens

## Perfis

| Perfil | Funcionalidades |
|---|---|
| Utilizador | Criar pedidos (categoria, localização, descrição, fotografia), consultar estado, histórico de concluídos, cancelar pedidos não concluídos |
| Administrador | Ver todos os pedidos, alterar estado, eliminar, gerir categorias (CRUD), estatísticas |

Estados de um pedido: **Submetido → Em análise → Concluído / Rejeitado**.

## Como executar

1. Criar um projeto gratuito em [supabase.com](https://supabase.com).
2. No painel do Supabase, abrir **SQL Editor** e executar o conteúdo de
   [`supabase/schema.sql`](supabase/schema.sql).
3. Em **Authentication → Sign In / Up → Email**, desativar a opção
   **Confirm email** (para permitir registo imediato sem confirmação).
4. Copiar as credenciais de **Settings → API** para o ficheiro
   `local.properties` na raiz do projeto:
   ```properties
   SUPABASE_URL=https://xxxx.supabase.co
   SUPABASE_ANON_KEY=eyJ...
   ```
5. Abrir o projeto no **Android Studio** e executar num emulador ou
   dispositivo com Android 8.0 (API 26) ou superior.

## Funcionalidades de valorização implementadas

- Pesquisa de pedidos (texto livre)
- Filtro por estado (chips)
- Ordenação por data (ascendente/descendente)
- Suporte a dois idiomas: PT (padrão) e EN (segue o idioma do dispositivo)
- Modo escuro (interruptor no perfil, persistido)
- Estatísticas simples (contagem de pedidos por estado)
- Adaptação a portrait e landscape (layouts com scroll)
