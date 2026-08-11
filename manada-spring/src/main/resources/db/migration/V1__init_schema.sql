-- ============================================================
-- MANADA — Esquema inicial (PostgreSQL / Flyway)
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- para gen_random_uuid()

-- ---------------- 1. CUENTAS Y PERFILES ----------------

CREATE TABLE app_user (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email         VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  full_name     VARCHAR(255) NOT NULL,
  phone         VARCHAR(50),
  account_type  VARCHAR(20) NOT NULL CHECK (account_type IN ('DUENO','FUNDACION','PROVEEDOR')),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE foundation_profile (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id             UUID UNIQUE NOT NULL REFERENCES app_user(id),
  org_name            VARCHAR(255) NOT NULL,
  tax_id              VARCHAR(50),
  city                VARCHAR(120) NOT NULL,
  documents_url       VARCHAR(500),
  verification_status VARCHAR(30) NOT NULL DEFAULT 'SIN_VERIFICAR'
                         CHECK (verification_status IN ('SIN_VERIFICAR','EN_REVISION','VERIFICADO','RECHAZADO')),
  created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE provider_profile (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id             UUID UNIQUE NOT NULL REFERENCES app_user(id),
  provider_type       VARCHAR(30) NOT NULL
                         CHECK (provider_type IN ('TIENDA','VETERINARIA','PASEADOR','PELUQUERIA','GUARDERIA','ADIESTRADOR')),
  business_name       VARCHAR(255) NOT NULL,
  tax_id              VARCHAR(50),
  city                VARCHAR(120) NOT NULL,
  documents_url       VARCHAR(500),
  commission_rate     NUMERIC(5,4) NOT NULL DEFAULT 0.15,
  verification_status VARCHAR(30) NOT NULL DEFAULT 'SIN_VERIFICAR'
                         CHECK (verification_status IN ('SIN_VERIFICAR','DOCUMENTOS_EN_REVISION','VERIFICADO','VERIFICACION_RECHAZADA','SUSPENDIDO')),
  created_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------- 2. MASCOTAS ----------------

CREATE TABLE pet (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  owner_id   UUID NOT NULL REFERENCES app_user(id),
  name       VARCHAR(120) NOT NULL,
  species    VARCHAR(10) NOT NULL CHECK (species IN ('PERRO','GATO','OTRO')),
  sex        VARCHAR(10) NOT NULL CHECK (sex IN ('MACHO','HEMBRA')),
  size       VARCHAR(10) CHECK (size IN ('PEQUENO','MEDIANO','GRANDE')),
  breed      VARCHAR(120),
  birth_year INTEGER,
  photo_url  VARCHAR(500),
  qr_code    UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_pet_owner ON pet(owner_id);

-- ---------------- 3. SOS — MASCOTAS PERDIDAS ----------------

CREATE TABLE lost_pet_alert (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  pet_id            UUID NOT NULL REFERENCES pet(id),
  reporter_id       UUID NOT NULL REFERENCES app_user(id),
  status            VARCHAR(30) NOT NULL DEFAULT 'PERDIDO'
                       CHECK (status IN ('PERDIDO','ENCONTRADO','AVISTAMIENTO_REPORTADO','RESUELTO')),
  last_seen_address VARCHAR(500) NOT NULL,
  locality          VARCHAR(120),
  latitude          DOUBLE PRECISION,
  longitude         DOUBLE PRECISION,
  radius_km         NUMERIC(5,2) NOT NULL DEFAULT 2,
  contact_phone     VARCHAR(50) NOT NULL,
  notes             TEXT,
  notify_push       BOOLEAN NOT NULL DEFAULT TRUE,
  notify_email      BOOLEAN NOT NULL DEFAULT TRUE,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  resolved_at       TIMESTAMPTZ
);
CREATE INDEX idx_alert_status ON lost_pet_alert(status);

CREATE TABLE sighting (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  alert_id    UUID NOT NULL REFERENCES lost_pet_alert(id),
  reporter_id UUID NOT NULL REFERENCES app_user(id),
  note        TEXT,
  photo_url   VARCHAR(500),
  latitude    DOUBLE PRECISION,
  longitude   DOUBLE PRECISION,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------- 4. ADOPCIÓN ----------------

CREATE TABLE adoption_listing (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  foundation_id UUID NOT NULL REFERENCES foundation_profile(id),
  name          VARCHAR(120) NOT NULL,
  species       VARCHAR(10) NOT NULL CHECK (species IN ('PERRO','GATO','OTRO')),
  sex           VARCHAR(10) NOT NULL CHECK (sex IN ('MACHO','HEMBRA')),
  size          VARCHAR(10) CHECK (size IN ('PEQUENO','MEDIANO','GRANDE')),
  age_label     VARCHAR(50),
  description   TEXT,
  photo_url     VARCHAR(500),
  status        VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE'
                   CHECK (status IN ('DISPONIBLE','EN_PROCESO','ADOPTADO')),
  created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_adoption_foundation ON adoption_listing(foundation_id);

CREATE TABLE adoption_request (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  listing_id   UUID NOT NULL REFERENCES adoption_listing(id),
  applicant_id UUID NOT NULL REFERENCES app_user(id),
  message      TEXT,
  status       VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                  CHECK (status IN ('PENDIENTE','EN_REVISION','APROBADA','RECHAZADA')),
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------- 5. MARKETPLACE (productos) ----------------

CREATE TABLE product (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  provider_id UUID NOT NULL REFERENCES provider_profile(id),
  name        VARCHAR(255) NOT NULL,
  category    VARCHAR(30) NOT NULL CHECK (category IN
                ('ALIMENTO','JUGUETES','ROPA_ACCESORIOS','CORREAS_ARNESES','HIGIENE_ASEO',
                 'SALUD','CAMAS_TRANSPORTADORAS','PLACAS_IDENTIFICACION','OTROS')),
  price       NUMERIC(12,2) NOT NULL,
  stock       INTEGER NOT NULL DEFAULT 0,
  photo_url   VARCHAR(500),
  active      BOOLEAN NOT NULL DEFAULT TRUE,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_product_provider ON product(provider_id);

CREATE TABLE purchase_order (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  buyer_id       UUID NOT NULL REFERENCES app_user(id),
  status         VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                    CHECK (status IN ('PENDIENTE','PAGADO','ENVIADO','ENTREGADO','CANCELADO')),
  subtotal       NUMERIC(12,2) NOT NULL,
  commission_amt NUMERIC(12,2) NOT NULL,
  total          NUMERIC(12,2) NOT NULL,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE order_item (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  order_id    UUID NOT NULL REFERENCES purchase_order(id),
  product_id  UUID NOT NULL REFERENCES product(id),
  quantity    INTEGER NOT NULL,
  unit_price  NUMERIC(12,2) NOT NULL
);

-- ---------------- 6. SERVICIOS (veterinaria, paseador, etc.) ----------------

CREATE TABLE service_offering (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  provider_id  UUID NOT NULL REFERENCES provider_profile(id),
  service_type VARCHAR(30) NOT NULL CHECK (service_type IN
                 ('VETERINARIA_GENERAL','URGENCIAS_24H','VACUNACION','CIRUGIA','ODONTOLOGIA',
                  'ESTETICA_BANO_CORTE','PASEO','GUARDERIA_HOSPEDAJE','ADIESTRAMIENTO','ESTERILIZACION')),
  name         VARCHAR(255) NOT NULL,
  price        NUMERIC(12,2) NOT NULL,
  duration_min INTEGER NOT NULL DEFAULT 30,
  active       BOOLEAN NOT NULL DEFAULT TRUE,
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_service_provider ON service_offering(provider_id);

CREATE TABLE booking (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  service_id     UUID NOT NULL REFERENCES service_offering(id),
  buyer_id       UUID NOT NULL REFERENCES app_user(id),
  scheduled_at   TIMESTAMPTZ NOT NULL,
  status         VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                    CHECK (status IN ('PENDIENTE','CONFIRMADA','COMPLETADA','CANCELADA')),
  price          NUMERIC(12,2) NOT NULL,
  commission_amt NUMERIC(12,2) NOT NULL,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ---------------- 7. COMUNIDAD ----------------

CREATE TABLE community_post (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  author_id  UUID NOT NULL REFERENCES app_user(id),
  type       VARCHAR(20) NOT NULL CHECK (type IN ('RESCATE','ADOPCION','DENUNCIA','HISTORIA','EVENTO')),
  caption    TEXT NOT NULL,
  photo_url  VARCHAR(500),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE post_comment (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  post_id    UUID NOT NULL REFERENCES community_post(id),
  author_id  UUID NOT NULL REFERENCES app_user(id),
  text       TEXT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE post_like (
  id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  post_id  UUID NOT NULL REFERENCES community_post(id),
  user_id  UUID NOT NULL REFERENCES app_user(id),
  UNIQUE (post_id, user_id)
);

-- ---------------- 8. MENSAJERÍA ----------------

CREATE TABLE conversation (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  context_type VARCHAR(50),
  context_ref  VARCHAR(255),
  created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE conversation_participant (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  conversation_id UUID NOT NULL REFERENCES conversation(id),
  user_id         UUID NOT NULL REFERENCES app_user(id),
  UNIQUE (conversation_id, user_id)
);

CREATE TABLE message (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  conversation_id UUID NOT NULL REFERENCES conversation(id),
  sender_id       UUID NOT NULL REFERENCES app_user(id),
  text            TEXT NOT NULL,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_message_conversation ON message(conversation_id);

-- ---------------- 9. NOTIFICACIONES ----------------

CREATE TABLE notification (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    UUID NOT NULL REFERENCES app_user(id),
  type       VARCHAR(20) NOT NULL CHECK (type IN ('SOS','ADOPCION','MENSAJE','PEDIDO','RESERVA','SISTEMA')),
  title      VARCHAR(255) NOT NULL,
  body       TEXT NOT NULL,
  read       BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_notification_user ON notification(user_id);
