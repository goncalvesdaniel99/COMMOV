-- Perfis dos utilizadores (ligados a auth.users)
create table if not exists public.profiles (
    id uuid primary key references auth.users (id) on delete cascade,
    nome text not null default '',
    email text not null default '',
    tipo_perfil text not null default 'utilizador' check (tipo_perfil in ('utilizador', 'admin')),
    created_at timestamptz not null default now()
);

-- Categorias de pedidos (geridas pelo administrador)
create table if not exists public.categorias (
    id bigint generated always as identity primary key,
    nome text not null unique
);

-- Pedidos / ocorrências
create table if not exists public.pedidos (
    id bigint generated always as identity primary key,
    user_id uuid not null references public.profiles (id) on delete cascade,
    categoria_id bigint references public.categorias (id) on delete set null,
    localizacao text not null,
    descricao text not null,
    estado text not null default 'submetido'
        check (estado in ('submetido', 'em_analise', 'concluido', 'rejeitado')),
    foto_url text,
    created_at timestamptz not null default now()
);

-- Categorias iniciais de exemplo
insert into public.categorias (nome) values
    ('Limpeza'),
    ('Manutenção'),
    ('Informática'),
    ('Segurança'),
    ('Outros')
on conflict (nome) do nothing;

-- Políticas simples: qualquer utilizador autenticado pode ler e escrever


alter table public.profiles enable row level security;
alter table public.categorias enable row level security;
alter table public.pedidos enable row level security;

drop policy if exists "profiles_all" on public.profiles;
create policy "profiles_all" on public.profiles
    for all to authenticated using (true) with check (true);

drop policy if exists "categorias_all" on public.categorias;
create policy "categorias_all" on public.categorias
    for all to authenticated using (true) with check (true);

drop policy if exists "pedidos_all" on public.pedidos;
create policy "pedidos_all" on public.pedidos
    for all to authenticated using (true) with check (true);


-- Armazenamento: bucket público para as fotografias dos pedidos


insert into storage.buckets (id, name, public)
values ('fotos', 'fotos', true)
on conflict (id) do nothing;

drop policy if exists "fotos_upload" on storage.objects;
create policy "fotos_upload" on storage.objects
    for insert to authenticated with check (bucket_id = 'fotos');

drop policy if exists "fotos_read" on storage.objects;
create policy "fotos_read" on storage.objects
    for select to public using (bucket_id = 'fotos');
