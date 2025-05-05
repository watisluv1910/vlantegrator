create schema if not exists "$DB_SCHEMA";
set search_path to "$DB_SCHEMA";

create type adapter_direction as enum ('OUTBOUND', 'INBOUND', 'COMMON');
create type channel_kind as enum ('CHANNEL', 'GATEWAY', 'NONE');
create type edge_type as enum ('default', 'straight', 'step', 'smoothstep', 'simplebezier');
create type marker_type as enum ('arrow', 'arrowclosed');
create type node_role as enum ('default', 'input', 'output', 'group');
create type network_driver as enum ('bridge', 'host', 'none', 'overlay', 'ipvlan', 'macvlan');

create table if not exists vlt_route
(
    id              uuid  not null primary key default pg_catalog.gen_random_uuid(),
    version_hash    text  not null unique,
    name            text  not null unique,
    description     text  not null,
    owner_name      text  not null,
    published_ports text  not null,
    env             jsonb not null
);

create table if not exists vlt_adapter
(
    id           uuid              not null primary key default pg_catalog.gen_random_uuid(),
    name         text              not null unique,
    display_name text              not null,
    description  text              not null,
    clazz        text              not null,
    type         text              not null,
    direction    adapter_direction not null,
    channel_kind channel_kind      not null
);

create table if not exists vlt_node
(
    id             uuid  not null primary key default pg_catalog.gen_random_uuid(),
    vlt_route_id   uuid  not null references vlt_route,
    vlt_adapter_id uuid  not null references vlt_adapter,
    name           text  not null,
    config         jsonb not null
);

create index if not exists vlt_node_route_idx on vlt_node (vlt_route_id);
create index if not exists vlt_node_adapter_idx on vlt_node (vlt_adapter_id);

create table if not exists vlt_node_position
(
    vlt_node_id uuid primary key not null references vlt_node,
    coord_x     bigint           not null default 0,
    coord_y     bigint           not null default 0,
    z_index     bigint           not null default 0
);

create table if not exists vlt_node_style
(
    vlt_node_id uuid      not null primary key references vlt_node,
    type        node_role not null default 'default',
    style       jsonb     not null
);

create table if not exists vlt_node_connection
(
    id        uuid not null primary key default pg_catalog.gen_random_uuid(),
    source_id uuid not null references vlt_node,
    target_id uuid not null unique references vlt_node
);

create unique index if not exists vlt_node_conn_src_uq on vlt_node_connection (source_id);

create table if not exists vlt_node_connection_style
(
    vlt_node_connection_id uuid        not null primary key references vlt_node_connection,
    type                   edge_type   not null default 'default',
    marker_start_type      marker_type not null default 'arrow',
    marker_end_type        marker_type not null default 'arrowclosed',
    animated               boolean     not null default false,
    focusable              boolean     not null default false
);

create table if not exists vlt_route_network
(
    id     uuid           not null primary key default pg_catalog.gen_random_uuid(),
    name   varchar(128)   not null,
    driver network_driver not null             default 'bridge'
);

create table if not exists vlt_route_networks
(
    vlt_route_id         uuid not null references vlt_route,
    vlt_route_network_id uuid not null references vlt_route_network,
    primary key (vlt_route_id, vlt_route_network_id)
);

comment on table vlt_route is 'Маршруты (integration flows)';
comment on column vlt_route.id is 'UUID маршрута';
comment on column vlt_route.name is 'Уникальное имя маршрута';
comment on column vlt_route.description is 'Описание назначения маршрута';
comment on column vlt_route.owner_name is 'Владелец / создатель маршрута';
comment on column vlt_route.published_ports is 'Порты «host:container»';
comment on column vlt_route.env is 'JSON-объект с переменными окружения';

comment on table vlt_adapter is 'Справочник типов адаптеров';
comment on column vlt_adapter.id is 'UUID адаптера';
comment on column vlt_adapter.name is 'Системное имя (уникально)';
comment on column vlt_adapter.display_name is 'Отображаемое имя';
comment on column vlt_adapter.clazz is 'Полное имя Java-класса';
comment on column vlt_adapter.type is 'Тип адаптера';
comment on column vlt_adapter.direction is 'Направление (IN/OUT/COMMON)';
comment on column vlt_adapter.channel_kind is 'Тип канала (CHANNEL/GATEWAY/NONE)';

comment on table vlt_node is 'Узлы маршрутов';
comment on column vlt_node.id is 'UUID узла';
comment on column vlt_node.vlt_route_id is 'FK → vlt_route';
comment on column vlt_node.vlt_adapter_id is 'FK → vlt_adapter';
comment on column vlt_node.name is 'Имя узла в редакторе';
comment on column vlt_node.config is 'Конфигурация адаптера (JSON)';

comment on table vlt_node_position is 'Координаты узлов на Canvas';
comment on column vlt_node_position.vlt_node_id is 'PK/FK → vlt_node';
comment on column vlt_node_position.coord_x is 'Координата X';
comment on column vlt_node_position.coord_y is 'Координата Y';
comment on column vlt_node_position.z_index is 'Слой (z-index)';

comment on table vlt_node_style is 'Дополнительный CSS-стиль узлов';
comment on column vlt_node_style.vlt_node_id is 'PK/FK → vlt_node';
comment on column vlt_node_style.type is 'Роль узла';
comment on column vlt_node_style.style is 'Стиль (JSON)';

comment on table vlt_node_connection is 'Связи между узлами';
comment on column vlt_node_connection.id is 'UUID связи';
comment on column vlt_node_connection.source_id is 'Источник';
comment on column vlt_node_connection.target_id is 'Приёмник (уникален)';

comment on table vlt_node_connection_style is 'Стили связей';
comment on column vlt_node_connection_style.vlt_node_connection_id is 'PK/FK → vlt_node_connection';
comment on column vlt_node_connection_style.type is 'Тип рёбра';
comment on column vlt_node_connection_style.marker_start_type is 'Маркер начала';
comment on column vlt_node_connection_style.marker_end_type is 'Маркер конца';
comment on column vlt_node_connection_style.animated is 'Анимировать ли линию';
comment on column vlt_node_connection_style.focusable is 'Доступно ли для фокуса';

comment on table vlt_route_network is 'Docker-сети маршрутов';
comment on column vlt_route_network.id is 'UUID сети';
comment on column vlt_route_network.name is 'Имя сети';
comment on column vlt_route_network.driver is 'Драйвер сети';

comment on table vlt_route_networks is 'Связь маршрутов и сетей';
comment on column vlt_route_networks.vlt_route_id is 'FK → vlt_route';
comment on column vlt_route_networks.vlt_route_network_id is 'FK → vlt_route_network';