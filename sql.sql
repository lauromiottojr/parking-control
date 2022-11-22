truncate table tb_user;
truncate table tb_users_roles;
use parking_control;

insert into tb_user values (1, '$2a$10$szdEDiwa8snlxsD2MJKOr.Ez.74vtXm7rpGnGI66wKntBgCT7E7y2', 'lauro');

insert into tb_user values (2, '$2a$10$szdEDiwa8snlxsD2MJKOr.Ez.74vtXm7rpGnGI66wKntBgCT7E7y2', 'joao');

select * from tb_user;

select * from tb_role;

select * from tb_parking_spot;

insert into tb_role values (1, 'ROLE_ADMIN');

insert into tb_role values (2, 'ROLE_USER');

insert into tb_users_roles values (1, 1);

insert into tb_users_roles values (2, 2);

select * from tb_users_roles;