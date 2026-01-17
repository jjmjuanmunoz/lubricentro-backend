-- ============================================================
-- V12: Add translate_key column to menus table
-- ============================================================

ALTER TABLE menus 
ADD COLUMN translate_key VARCHAR(255);

CREATE INDEX idx_menus_translate_key ON menus(translate_key);
