
CREATE UNIQUE INDEX IF NOT EXISTS idx_u_users_email_non_deleted
ON users(email) 
WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_u_users_username_non_deleted 
ON users(username) 
WHERE deleted_at IS NULL;