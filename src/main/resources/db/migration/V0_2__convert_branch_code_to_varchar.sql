ALTER TABLE stockflow.branches
    ALTER COLUMN code TYPE varchar(3)
        USING trim(both from code);