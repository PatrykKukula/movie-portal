INSERT INTO role (role_name, role_id) VALUES ('USER',1) ON CONFLICT (role_id) DO NOTHING;
INSERT INTO role (role_name, role_id) VALUES  ('ADMIN',2) ON CONFLICT (role_id) DO NOTHING;

INSERT INTO user_entity (username, email, password, user_id, is_enabled)
VALUES ('Admin','admin@admin.com', '$2a$10$kooU2aD8hJ4OtlrSEL5LMONnB/sAhewavZl02mn1FQJoieA0o5PLu',1, true)
ON CONFLICT (user_id) DO NOTHING ;