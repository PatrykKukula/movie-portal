INSERT INTO role (role_name, role_id) VALUES ('USER',1) ON CONFLICT (role_id) DO NOTHING;
INSERT INTO role (role_name, role_id) VALUES  ('ADMIN',2) ON CONFLICT (role_id) DO NOTHING;
INSERT INTO role (role_name, role_id) VALUES  ('MODERATOR',3) ON CONFLICT (role_id) DO NOTHING;

INSERT INTO user_entity (username, email, password, is_enabled, registered_at)
VALUES ('Admin','admin@admin.pl', '{noop}Admin123!', true, '2024-12-12 00:00:00')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id) VALUES (1, 2) ON CONFLICT (user_id, role_id) DO NOTHING;
--INSERT INTO user_roles (user_id, role_id)
--SELECT 1, 2
--WHERE NOT EXISTS (
--    SELECT 1 FROM user_roles
--    WHERE user_id = 1 AND role_id = 2
--);