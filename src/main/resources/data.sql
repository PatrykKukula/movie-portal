INSERT INTO role (role_name, role_id) VALUES ('USER',1) ON CONFLICT (role_id) DO NOTHING;
INSERT INTO role (role_name, role_id) VALUES  ('ADMIN',2) ON CONFLICT (role_id) DO NOTHING;

INSERT INTO user_entity (username, email, password, is_enabled, registered_at)
VALUES ('Admin','admin@admin.pl', '{noop}Admin123!', true, '2024-12-12 00:00:00')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id) VALUES (1,2) ON CONFLICT DO NOTHING;