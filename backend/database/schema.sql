-- =====================================================================
-- PlaceNextAI - MySQL Schema (matches the JPA entities)
-- Note: the application also creates/updates this schema automatically
-- on startup (spring.jpa.hibernate.ddl-auto=update).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS placenextai_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE placenextai_db;

CREATE TABLE IF NOT EXISTS students (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    full_name       VARCHAR(100)  NOT NULL,
    email           VARCHAR(120)  NOT NULL,
    password        VARCHAR(255)  NOT NULL,
    phone           VARCHAR(15),
    college         VARCHAR(150),
    branch          VARCHAR(80),
    graduation_year INT,
    cgpa            DOUBLE,
    skills          VARCHAR(500),
    role            VARCHAR(30)   NOT NULL,
    created_at      DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_students_email (email)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS recruiters (
    id             BIGINT NOT NULL AUTO_INCREMENT,
    company_name   VARCHAR(150) NOT NULL,
    recruiter_name VARCHAR(100) NOT NULL,
    email          VARCHAR(120) NOT NULL,
    password       VARCHAR(255) NOT NULL,
    designation    VARCHAR(100),
    role           VARCHAR(30)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_recruiters_email (email)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS admins (
    id       BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(80)  NOT NULL,
    email    VARCHAR(120) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(30)  NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_admins_email (email)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS jobs (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    title           VARCHAR(120)  NOT NULL,
    company         VARCHAR(150)  NOT NULL,
    location        VARCHAR(120),
    description     VARCHAR(3000),
    salary          VARCHAR(60),
    skills_required VARCHAR(500),
    created_date    DATE          NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS applications (
    id           BIGINT NOT NULL AUTO_INCREMENT,
    student_id   BIGINT      NOT NULL,
    job_id       BIGINT      NOT NULL,
    status       VARCHAR(30) NOT NULL,
    applied_date DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_applications_student_job (student_id, job_id),
    CONSTRAINT fk_applications_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs (id)
) ENGINE = InnoDB;
