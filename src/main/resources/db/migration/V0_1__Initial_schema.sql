CREATE TABLE IF NOT EXISTS stockflow.tenants
(
    id                 UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    created_by         VARCHAR(64)  NOT NULL DEFAULT 'system',
    created_date       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    last_modified_by   VARCHAR(64),
    last_modified_date TIMESTAMPTZ,

    name               VARCHAR(256) NOT NULL,
    slug               VARCHAR(64)  NOT NULL UNIQUE,

    active             BOOLEAN      NOT NULL DEFAULT TRUE
);


CREATE TABLE IF NOT EXISTS stockflow.organizations
(
    id                  UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    tenant_id           UUID         NOT NULL REFERENCES stockflow.tenants (id) ON DELETE CASCADE,
    created_by          VARCHAR(64)  NOT NULL DEFAULT 'system',
    created_date        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    last_modified_by    VARCHAR(64),
    last_modified_date  TIMESTAMPTZ,

    tin                 VARCHAR(13)  NOT NULL, -- RUC/TIN (único a nivel país)
    name                VARCHAR(256) NOT NULL,
    address             TEXT         NOT NULL,
    email               TEXT       NOT NULL,
    phone               VARCHAR(20),
    marketplace_enabled BOOLEAN      NOT NULL DEFAULT FALSE,

    active              BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_organizations_tin UNIQUE (tin),
    CONSTRAINT ux_organizations_id_tenant UNIQUE (id, tenant_id)
);
CREATE INDEX IF NOT EXISTS idx_organizations_tenant ON stockflow.organizations (tenant_id);
CREATE INDEX IF NOT EXISTS idx_organizations_email_tenant ON stockflow.organizations (tenant_id, email);


CREATE TABLE IF NOT EXISTS stockflow.businesses
(
    id                  UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    tenant_id           UUID         NOT NULL REFERENCES stockflow.tenants (id) ON DELETE CASCADE,
    created_by          VARCHAR(64)  NOT NULL DEFAULT 'system',
    created_date        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    last_modified_by    VARCHAR(64),
    last_modified_date  TIMESTAMPTZ,

    organization_id     UUID         NOT NULL,
    name                VARCHAR(256) NOT NULL,
    address             TEXT         NOT NULL,
    email               TEXT,
    phone               VARCHAR(20),
    marketplace_enabled BOOLEAN      NOT NULL DEFAULT FALSE,

    active              BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT ux_businesses_id_tenant UNIQUE (id, tenant_id),
    CONSTRAINT fk_businesses_org_tenant FOREIGN KEY (organization_id, tenant_id)
        REFERENCES stockflow.organizations (id, tenant_id) ON DELETE RESTRICT
);
CREATE INDEX IF NOT EXISTS idx_businesses_tenant ON stockflow.businesses (tenant_id);
CREATE INDEX IF NOT EXISTS idx_businesses_email_tenant ON stockflow.businesses (tenant_id, email);


CREATE TABLE IF NOT EXISTS stockflow.branches
(
    id                 UUID PRIMARY KEY      DEFAULT uuid_generate_v4(),
    tenant_id          UUID         NOT NULL REFERENCES stockflow.tenants (id) ON DELETE CASCADE,
    created_by         VARCHAR(64)  NOT NULL DEFAULT 'system',
    created_date       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    last_modified_by   VARCHAR(64),
    last_modified_date TIMESTAMPTZ,

    business_id        UUID         NOT NULL,
    code               CHAR(3)      NOT NULL,
    name               VARCHAR(256) NOT NULL,
    phone              VARCHAR(20),
    email              TEXT,
    address            TEXT,
    is_main            BOOLEAN      NOT NULL DEFAULT FALSE,

    active             BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_branch_code_length CHECK (char_length(code) = 3),
    CONSTRAINT ux_branches_id_tenant UNIQUE (id, tenant_id),
    CONSTRAINT fk_branches_business_tenant FOREIGN KEY (business_id, tenant_id)
        REFERENCES stockflow.businesses (id, tenant_id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_branch_org_code ON stockflow.branches (business_id, lower(code));
CREATE UNIQUE INDEX IF NOT EXISTS ux_branches_business_main ON stockflow.branches (business_id) WHERE is_main = true;
CREATE INDEX IF NOT EXISTS idx_branches_tenant ON stockflow.branches (tenant_id);
CREATE INDEX IF NOT EXISTS idx_branches_business ON stockflow.branches (business_id);


CREATE TABLE IF NOT EXISTS stockflow.persons
(
    id                    UUID PRIMARY KEY     DEFAULT uuid_generate_v4(),
    created_by            VARCHAR(64) NOT NULL DEFAULT 'system',
    created_date          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_modified_by      VARCHAR(64),
    last_modified_date    TIMESTAMPTZ,

    first_name            TEXT        NOT NULL,
    second_name           TEXT,
    surname               TEXT        NOT NULL,
    second_surname        TEXT,
    identification_type   TEXT      NOT NULL,
    identification_number TEXT      NOT NULL,
    language              VARCHAR(5)  NOT NULL DEFAULT 'ES',

    active                BOOLEAN     NOT NULL DEFAULT TRUE,

    CONSTRAINT ux_persons_idtype_number UNIQUE (identification_type, identification_number)
);

CREATE INDEX IF NOT EXISTS idx_persons_identification ON stockflow.persons (identification_number);
CREATE INDEX IF NOT EXISTS idx_persons_name ON stockflow.persons (lower(surname), lower(first_name));


CREATE TABLE IF NOT EXISTS stockflow.person_contacts
(
    id                 UUID PRIMARY KEY     DEFAULT uuid_generate_v4(),
    created_by         VARCHAR(64) NOT NULL DEFAULT 'system',
    created_date       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_modified_by   VARCHAR(64),
    last_modified_date TIMESTAMPTZ,

    person_id          UUID        NOT NULL REFERENCES stockflow.persons (id) ON DELETE CASCADE,
    type               VARCHAR(20) NOT NULL,
    value              TEXT        NOT NULL,
    is_primary         BOOLEAN     NOT NULL DEFAULT FALSE,
    verified           BOOLEAN     NOT NULL DEFAULT FALSE
);
CREATE INDEX IF NOT EXISTS idx_person_contacts_person ON stockflow.person_contacts (person_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_person_contacts_person_type_primary ON stockflow.person_contacts (person_id, type) WHERE is_primary;


CREATE TABLE IF NOT EXISTS stockflow.person_addresses
(
    id                 UUID PRIMARY KEY     DEFAULT uuid_generate_v4(),
    created_by         VARCHAR(64) NOT NULL DEFAULT 'system',
    created_date       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_modified_by   VARCHAR(64),
    last_modified_date TIMESTAMPTZ,

    person_id          UUID        NOT NULL REFERENCES stockflow.persons (id) ON DELETE CASCADE,
    type               VARCHAR(20),
    country            TEXT,
    state              TEXT,
    city               TEXT,
    postal_code        TEXT,
    street             TEXT,
    number             TEXT,
    extras             TEXT,
    is_primary         BOOLEAN     NOT NULL DEFAULT FALSE,

    active             BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_person_addresses_person ON stockflow.person_addresses (person_id);
CREATE UNIQUE INDEX IF NOT EXISTS ux_person_addresses_person_type_primary ON stockflow.person_addresses (person_id, type)  WHERE is_primary;


CREATE TABLE IF NOT EXISTS stockflow.users
(
    id                 UUID                  DEFAULT uuid_generate_v4() PRIMARY KEY,
    created_by         VARCHAR(64)  NOT NULL DEFAULT 'system',
    created_date       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    last_modified_by   VARCHAR(64),
    last_modified_date TIMESTAMPTZ,

    email              TEXT       NOT NULL,
    password           VARCHAR(255) NOT NULL,
    auth_provider      VARCHAR(32)  NOT NULL,
    locked             BOOLEAN      NOT NULL DEFAULT false,
    person_id          UUID         NOT NULL REFERENCES stockflow.persons (id),
    branch_id          UUID         REFERENCES stockflow.branches (id) ON DELETE SET NULL,
    last_login         TIMESTAMPTZ,

    active             BOOLEAN      NOT NULL DEFAULT true,

    CONSTRAINT uq_users_email UNIQUE (email)
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email_lower ON stockflow.users (lower(email));
CREATE INDEX IF NOT EXISTS idx_users_branch ON stockflow.users (branch_id);
CREATE INDEX IF NOT EXISTS idx_users_person ON stockflow.users (person_id);


CREATE TABLE IF NOT EXISTS stockflow.system_permissions
(
    id                 UUID                 DEFAULT uuid_generate_v4() PRIMARY KEY,
    created_by         VARCHAR(64) NOT NULL DEFAULT 'system',
    created_date       TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_modified_by   VARCHAR(64),
    last_modified_date TIMESTAMPTZ,

    name               TEXT        NOT NULL UNIQUE,
    description        TEXT
);


CREATE TABLE IF NOT EXISTS stockflow.system_roles
(
    id                 UUID                 DEFAULT uuid_generate_v4() PRIMARY KEY,
    created_by         VARCHAR(64) NOT NULL DEFAULT 'system',
    created_date       TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_modified_by   VARCHAR(64),
    last_modified_date TIMESTAMPTZ,

    name               TEXT        NOT NULL UNIQUE,
    description        TEXT
);


CREATE TABLE IF NOT EXISTS stockflow.role_permissions
(
    id                 UUID                 DEFAULT uuid_generate_v4() PRIMARY KEY,
    created_by         VARCHAR(64) NOT NULL DEFAULT 'system',
    created_date       TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_modified_by   VARCHAR(64),
    last_modified_date TIMESTAMPTZ,

    role_id            UUID REFERENCES stockflow.system_roles (id) ON DELETE CASCADE,
    permission_id      UUID REFERENCES stockflow.system_permissions (id) ON DELETE CASCADE,

    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
);
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission ON stockflow.role_permissions (permission_id);


CREATE TABLE IF NOT EXISTS stockflow.user_access_memberships
(
    id                 UUID                 DEFAULT uuid_generate_v4() PRIMARY KEY,
    created_by         VARCHAR(64) NOT NULL DEFAULT 'system',
    created_date       TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_modified_by   VARCHAR(64),
    last_modified_date TIMESTAMPTZ,

    user_id            UUID        NOT NULL REFERENCES stockflow.users (id) ON DELETE CASCADE,
    role_id            UUID        NOT NULL REFERENCES stockflow.system_roles (id) ON DELETE RESTRICT,
    scope_type         VARCHAR(20) NOT NULL,
    scope_id           UUID,
    tenant_id          UUID REFERENCES stockflow.tenants (id) ON DELETE CASCADE,

    CONSTRAINT ux_user_membership_unique UNIQUE (user_id, role_id, scope_type, scope_id),
    CONSTRAINT chk_user_access_scope_type CHECK (scope_type IN ('TENANT', 'ORGANIZATION', 'BUSINESS', 'BRANCH'))
);
CREATE INDEX IF NOT EXISTS idx_user_access_memberships_user ON stockflow.user_access_memberships (user_id);
CREATE INDEX IF NOT EXISTS idx_user_access_memberships_tenant ON stockflow.user_access_memberships (tenant_id);
CREATE INDEX IF NOT EXISTS idx_user_access_memberships_scope ON stockflow.user_access_memberships (scope_type, scope_id);
CREATE INDEX IF NOT EXISTS idx_user_access_memberships_role ON stockflow.user_access_memberships (role_id);


CREATE TABLE IF NOT EXISTS stockflow.refresh_tokens
(
    id           UUID PRIMARY KEY     DEFAULT uuid_generate_v4(),
    created_by   VARCHAR(64) NOT NULL DEFAULT 'system',
    created_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    tenant_id    UUID REFERENCES stockflow.tenants (id) ON DELETE CASCADE,
    user_id      UUID        NOT NULL REFERENCES stockflow.users (id) ON DELETE CASCADE,
    token_hash   TEXT        NOT NULL,
    expires_at   TIMESTAMPTZ NOT NULL,
    revoked      BOOLEAN     NOT NULL DEFAULT FALSE,
    device_info  JSONB,
    ip_address   INET,
    last_used_at TIMESTAMPTZ,

    CONSTRAINT ux_refresh_tokens_token_hash UNIQUE (token_hash)
);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user ON stockflow.refresh_tokens (user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_tenant ON stockflow.refresh_tokens (tenant_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_active ON stockflow.refresh_tokens (user_id) WHERE revoked = false;
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_expires_at_active ON stockflow.refresh_tokens (expires_at) WHERE revoked = false;
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_tenant_active ON stockflow.refresh_tokens (tenant_id) WHERE revoked = false;