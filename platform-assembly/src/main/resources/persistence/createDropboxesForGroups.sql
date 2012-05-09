-- admin
set @rootDisciplineId = (SELECT component_id FROM component_spec where external_key = '/Disciplines');
insert into component_spec (obj_version, component_name, external_key, component_type, shared, model_info, owner, component_id, creator_user_id, date_created) values (0, 'admin', null, 'gov.nasa.arc.mct.core.components.TelemetryDisciplineComponent', 1, null, 'admin', 'f58a7e37b0684835b8590feea7e88a4c','admin', NOW());
set @parentMaxSeq = ifnull(((SELECT MAX(seq_no) FROM component_relationship where component_id = @rootDisciplineId)) , 0);
insert into component_relationship (component_id, seq_no, associated_component_id) values (@rootDisciplineId, @parentMaxSeq + 1, 'f58a7e37b0684835b8590feea7e88a4c');
set @lastObjVersion = (SELECT max(obj_version) FROM  component_spec where component_id=@rootDisciplineId);
update component_spec set obj_version = (@lastObjVersion + 1) where component_id=@rootDisciplineId;
insert into component_spec (obj_version, component_name, external_key, component_type, shared, model_info, owner, component_id, creator_user_id, date_created) values (0, 'admin\'s Drop Boxes', null, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 1, null, 'admin', '875c466ec4a0499c80eac0b1b7090d75','admin', NOW());
insert into component_relationship (component_id, seq_no, associated_component_id) values ('f58a7e37b0684835b8590feea7e88a4c', 2, '875c466ec4a0499c80eac0b1b7090d75');
insert into component_spec (obj_version, component_name, external_key, component_type, shared, model_info, owner, component_id, creator_user_id, date_created) values (0, 'All admin\'s Drop Boxes', null, 'gov.nasa.arc.mct.core.components.TelemetryAllDropBoxComponent', 1, null, '*', 'eacc672459114d56a57f4455934e517e','admin', NOW());
insert into component_relationship (component_id, seq_no, associated_component_id) values ('875c466ec4a0499c80eac0b1b7090d75', 0, 'eacc672459114d56a57f4455934e517e');
-- TestUsers
set @rootDisciplineId = (SELECT component_id FROM component_spec where external_key = '/Disciplines');
insert into component_spec (obj_version, component_name, external_key, component_type, shared, model_info, owner, component_id, creator_user_id, date_created) values (0, 'TestUsers', null, 'gov.nasa.arc.mct.core.components.TelemetryDisciplineComponent', 1, null, 'admin', 'fccd86fef36c4225ba4e864408c0c626','admin', NOW());
set @parentMaxSeq = ifnull(((SELECT MAX(seq_no) FROM component_relationship where component_id = @rootDisciplineId)) , 0);
insert into component_relationship (component_id, seq_no, associated_component_id) values (@rootDisciplineId, @parentMaxSeq + 1, 'fccd86fef36c4225ba4e864408c0c626');
set @lastObjVersion = (SELECT max(obj_version) FROM  component_spec where component_id=@rootDisciplineId);
update component_spec set obj_version = (@lastObjVersion + 1) where component_id=@rootDisciplineId;
insert into component_spec (obj_version, component_name, external_key, component_type, shared, model_info, owner, component_id, creator_user_id, date_created) values (0, 'TestUsers\'s Drop Boxes', null, 'gov.nasa.arc.mct.components.collection.CollectionComponent', 1, null, 'admin', '5e892cba8ca14f319d79f4d53f72cebc','admin', NOW());
insert into component_relationship (component_id, seq_no, associated_component_id) values ('fccd86fef36c4225ba4e864408c0c626', 2, '5e892cba8ca14f319d79f4d53f72cebc');
insert into component_spec (obj_version, component_name, external_key, component_type, shared, model_info, owner, component_id, creator_user_id, date_created) values (0, 'All TestUsers\'s Drop Boxes', null, 'gov.nasa.arc.mct.core.components.TelemetryAllDropBoxComponent', 1, null, '*', '98ba714c8e864a1c8402d9ed3bd5b641','admin', NOW());
insert into component_relationship (component_id, seq_no, associated_component_id) values ('5e892cba8ca14f319d79f4d53f72cebc', 0, '98ba714c8e864a1c8402d9ed3bd5b641');
