CREATE UNIQUE INDEX IF NOT EXISTS idx_u_plan_tier_not_deleted
ON plans (tier)
WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_u_plan_code_not_deleted
ON plans (code)
WHERE deleted_at IS NULL;
