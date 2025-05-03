create database if not exists $DB_SCHEMA;
use $DB_SCHEMA;

create table if not exists vlt_route
(
    id              char(36)     not null comment 'уникальный идентификатор маршрута (uuid)',
    name            varchar(128) not null comment 'имя маршрута',
    description     text         not null comment 'подробное описание назначения маршрута',
    owner_name      varchar(255) not null comment 'имя владельца/создателя маршрута',
    published_ports varchar(255) not null comment 'маппинг портов «host:container» через запятую',
    env             json         not null comment 'json-объект с переменными окружения',
    primary key (id)
);
alter table vlt_route
    add unique vlt_route_name_unique (name);

create table if not exists vlt_node
(
    id             char(36)     not null comment 'uuid-узла',
    vlt_route_id   char(36)     not null comment 'ссылка на маршрут-владелец',
    vlt_adapter_id char(36)     not null comment 'тип адаптера (fk → vlt_adapter)',
    name           varchar(128) not null comment 'имя узла в редакторе',
    config         json         not null comment 'конфигурация адаптера в виде json',
    primary key (id)
);
alter table vlt_node
    add index vlt_node_vlt_route_id_index (vlt_route_id);
alter table vlt_node
    add index vlt_node_vlt_adapter_id_index (vlt_adapter_id);

create table if not exists vlt_node_position
(
    vlt_node_id char(36) not null comment 'pk=fk на vlt_node.id',
    coord_x     bigint   not null default 0 comment 'координата X на canvas',
    coord_y     bigint   not null default 0 comment 'координата Y на canvas',
    z_index     bigint   not null default 0 comment 'z-index (слой)',
    primary key (vlt_node_id)
);

create table if not exists vlt_adapter
(
    id           char(36)     not null comment 'uuid-адаптера',
    name         varchar(128) not null comment 'системное имя (уникально)',
    display_name varchar(255) not null comment 'человекочитаемое имя',
    class        varchar(255) not null comment 'полное имя java-класса',
    primary key (id)
);
alter table vlt_adapter
    add unique vlt_adapter_name_unique (name);

create table if not exists vlt_node_connection
(
    id        char(36) not null comment 'uuid-соединения',
    source_id char(36) not null comment 'fk → vlt_node.id (источник)',
    target_id char(36) not null comment 'fk → vlt_node.id (приёмник, уникален)',
    primary key (id)
);
alter table vlt_node_connection
    add unique vlt_node_connection_source_id_unique (source_id);
alter table vlt_node_connection
    add unique vlt_node_connection_target_id_unique (target_id);

create table if not exists vlt_node_connection_style
(
    vlt_node_connection_id char(36)                                                       not null comment 'pk=fk на vlt_node_connection.id',
    type                   enum ('default','straight','step','smoothstep','simplebezier') not null default 'default' comment 'тип рёбра на canvas',
    marker_start_type      enum ('arrow','arrowclosed')                                   not null default 'arrow' comment 'маркер начала',
    marker_end_type        enum ('arrow','arrowclosed')                                   not null default 'arrowclosed' comment 'маркер конца',
    animated               boolean                                                        not null default 0 comment 'анимировать ли линию',
    focusable              boolean                                                        not null default 0 comment 'доступна ли в фокусе',
    primary key (vlt_node_connection_id)
);

create table if not exists vlt_node_style
(
    vlt_node_id char(36)                                  not null comment 'pk=fk на vlt_node.id',
    type        enum ('default','input','output','group') not null default 'default' comment 'роль узла',
    style       json                                      not null comment 'кастомный css-стиль в json',
    primary key (vlt_node_id)
);

create table if not exists vlt_route_network
(
    id     char(36)                                                   not null comment 'uuid сети',
    name   varchar(128)                                               not null comment 'имя docker-сети',
    driver enum ('bridge','host','none','overlay','ipvlan','macvlan') not null default 'bridge' comment 'драйвер docker-сети',
    primary key (id)
);

create table if not exists vlt_route_networks
(
    vlt_route_id         char(36) not null comment 'fk → vlt_route.id',
    vlt_route_network_id char(36) not null comment 'fk → vlt_route_network.id',
    primary key (vlt_route_id, vlt_route_network_id)
);

alter table vlt_route_networks
    add constraint vlt_route_networks_vlt_route_network_id_fk
        foreign key (vlt_route_network_id) references vlt_route_network (id);

alter table vlt_node
    add constraint vlt_node_position_fk
        foreign key (id) references vlt_node_position (vlt_node_id);

alter table vlt_node_connection
    add constraint vlt_node_connection_source_fk
        foreign key (source_id) references vlt_node (id);

alter table vlt_node_connection
    add constraint vlt_node_connection_target_fk
        foreign key (target_id) references vlt_node (id);

alter table vlt_node
    add constraint vlt_node_adapter_fk
        foreign key (vlt_adapter_id) references vlt_adapter (id);

alter table vlt_node
    add constraint vlt_node_style_fk
        foreign key (id) references vlt_node_style (vlt_node_id);

alter table vlt_route_networks
    add constraint vlt_route_networks_route_fk
        foreign key (vlt_route_id) references vlt_route (id);

alter table vlt_node_connection
    add constraint vlt_node_connection_style_fk
        foreign key (id) references vlt_node_connection_style (vlt_node_connection_id);

alter table vlt_node
    add constraint vlt_node_route_fk
        foreign key (vlt_route_id) references vlt_route (id);