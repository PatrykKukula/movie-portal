INSERT INTO role (role_name, role_id) VALUES ('USER',1) ON CONFLICT (role_id) DO NOTHING;
INSERT INTO role (role_name, role_id) VALUES  ('ADMIN',2) ON CONFLICT (role_id) DO NOTHING;

INSERT INTO user_entity (username, email, password, user_id, is_enabled)
VALUES ('Admin','admin@admin.pl', '{noop}Admin123!',0, true)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id) VALUES (0,2) ON CONFLICT DO NOTHING;