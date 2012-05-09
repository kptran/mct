-- create root and top level nodes
insert into component_spec (obj_version, component_name, external_key, component_type, shared, model_info, owner, deleted, component_id, creator_user_id, date_created) values (0, 'All', '/', 'gov.nasa.arc.mct.core.components.TelemetryDataTaxonomyComponent', 0, null, 'admin', 0, '34c90c3068854cc0a85f11ad3c2b5708', 'admin', NOW());    

-- Disciplines 34c90c3068854cc0a85f11ad3c2b5710 at root 34c90c3068854cc0a85f11ad3c2b5708
insert  into component_spec (obj_version, component_name, external_key, component_type, shared, model_info, owner, deleted, component_id, creator_user_id, date_created) values (0, 'Disciplines', '/Disciplines', 'gov.nasa.arc.mct.core.components.TelemetryDataTaxonomyComponent', 1, null, 'admin', 0, '34c90c3068854cc0a85f11ad3c2b5710', 'admin', NOW());
insert  into component_relationship  (component_id, associated_component_id, seq_no) values ('34c90c3068854cc0a85f11ad3c2b5708', '34c90c3068854cc0a85f11ad3c2b5710', 1);
