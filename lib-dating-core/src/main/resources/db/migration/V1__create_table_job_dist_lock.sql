DO
$$
DECLARE
  retry BOOLEAN;
BEGIN
  ------------------------------------------------------------
  -- 2) Table job_dist_lock
  ------------------------------------------------------------
  IF NOT EXISTS (
    SELECT 1
      FROM information_schema.tables
     WHERE table_schema = 'dating_adm'
       AND table_name   = 'job_dist_lock'
  ) THEN
    retry := TRUE;
    WHILE retry LOOP
      BEGIN
        EXECUTE '
          CREATE TABLE dating_adm.job_dist_lock (
            idt_job_dist_lock      VARCHAR(100) NOT NULL PRIMARY KEY,
            dat_last_run           TIMESTAMP(6),
            dat_lock               TIMESTAMP(6),
            num_max_running_min    SMALLINT    NOT NULL,
            num_delay              SMALLINT,
            flg_active             SMALLINT
          )
        ';
        retry := FALSE;
      EXCEPTION
        WHEN SQLSTATE '55P03' THEN  -- lock_not_available
          PERFORM pg_sleep(1);
        WHEN OTHERS THEN
          RAISE;
      END;
    END LOOP;
  END IF;

  ------------------------------------------------------------
  -- 3) Índices
  ------------------------------------------------------------
  IF NOT EXISTS (
    SELECT 1 FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dating_adm' AND c.relname = 'jobdistlock_idx01'
  ) THEN
    retry := TRUE;
    WHILE retry LOOP
      BEGIN
        EXECUTE '
          CREATE INDEX jobdistlock_idx01
            ON dating_adm.job_dist_lock (dat_last_run)
        ';
        retry := FALSE;
      EXCEPTION
        WHEN SQLSTATE '55P03' THEN
          PERFORM pg_sleep(1);
        WHEN OTHERS THEN
          RAISE;
      END;
    END LOOP;
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dating_adm' AND c.relname = 'jobdistlock_idx02'
  ) THEN
    retry := TRUE;
    WHILE retry LOOP
      BEGIN
        EXECUTE '
          CREATE INDEX jobdistlock_idx02
            ON dating_adm.job_dist_lock (dat_lock)
        ';
        retry := FALSE;
      EXCEPTION
        WHEN SQLSTATE '55P03' THEN
          PERFORM pg_sleep(1);
        WHEN OTHERS THEN
          RAISE;
      END;
    END LOOP;
  END IF;

  ------------------------------------------------------------
  -- 4) Comentários
  ------------------------------------------------------------
  -- Tabela
  PERFORM 1
    FROM pg_catalog.pg_description d
    JOIN pg_catalog.pg_class c ON c.oid = d.objoid
    JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
   WHERE n.nspname = 'dating_adm'
     AND c.relname = 'job_dist_lock'
     AND d.objsubid = 0;
  IF NOT FOUND THEN
    EXECUTE '
      COMMENT ON TABLE dating_adm.job_dist_lock
        IS ''[NOT_SECURITY_APPLY] Tabela de configuração de Jobs.''
    ';
  END IF;

  -- Colunas
  PERFORM 1
    FROM pg_catalog.pg_description d
    JOIN pg_catalog.pg_class c ON c.oid = d.objoid
    JOIN pg_catalog.pg_attribute a ON a.attrelid = c.oid AND a.attnum = d.objsubid
    JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
   WHERE n.nspname = 'dating_adm'
     AND c.relname = 'job_dist_lock'
     AND a.attname = 'idt_job_dist_lock';
  IF NOT FOUND THEN
    EXECUTE '
      COMMENT ON COLUMN dating_adm.job_dist_lock.idt_job_dist_lock
        IS ''[NOT_SECURITY_APPLY] Chave primaria [JOBDISTLOCK_PK] Nome do Job.''
    ';
  END IF;

  PERFORM 1
    FROM pg_catalog.pg_description d
    JOIN pg_catalog.pg_class c ON c.oid = d.objoid
    JOIN pg_catalog.pg_attribute a ON a.attrelid = c.oid AND a.attnum = d.objsubid
    JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
   WHERE n.nspname = 'dating_adm'
     AND c.relname = 'job_dist_lock'
     AND a.attname = 'dat_last_run';
  IF NOT FOUND THEN
    EXECUTE '
      COMMENT ON COLUMN dating_adm.job_dist_lock.dat_last_run
        IS ''[NOT_SECURITY_APPLY] Data da última execução.''
    ';
  END IF;

  PERFORM 1
    FROM pg_catalog.pg_description d
    JOIN pg_catalog.pg_class c ON c.oid = d.objoid
    JOIN pg_catalog.pg_attribute a ON a.attrelid = c.oid AND a.attnum = d.objsubid
    JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
   WHERE n.nspname = 'dating_adm'
     AND c.relname = 'job_dist_lock'
     AND a.attname = 'dat_lock';
  IF NOT FOUND THEN
    EXECUTE '
      COMMENT ON COLUMN dating_adm.job_dist_lock.dat_lock
        IS ''[NOT_SECURITY_APPLY] Data de lock.''
    ';
  END IF;

  PERFORM 1
    FROM pg_catalog.pg_description d
    JOIN pg_catalog.pg_class c ON c.oid = d.objoid
    JOIN pg_catalog.pg_attribute a ON a.attrelid = c.oid AND a.attnum = d.objsubid
    JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
   WHERE n.nspname = 'dating_adm'
     AND c.relname = 'job_dist_lock'
     AND a.attname = 'num_max_running_min';
  IF NOT FOUND THEN
    EXECUTE '
      COMMENT ON COLUMN dating_adm.job_dist_lock.num_max_running_min
        IS ''[NOT_SECURITY_APPLY] Número máximo de minutos em execução.''
    ';
  END IF;

  PERFORM 1
    FROM pg_catalog.pg_description d
    JOIN pg_catalog.pg_class c ON c.oid = d.objoid
    JOIN pg_catalog.pg_attribute a ON a.attrelid = c.oid AND a.attnum = d.objsubid
    JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
   WHERE n.nspname = 'dating_adm'
     AND c.relname = 'job_dist_lock'
     AND a.attname = 'num_delay';
  IF NOT FOUND THEN
    EXECUTE '
      COMMENT ON COLUMN dating_adm.job_dist_lock.num_delay
        IS ''[NOT_SECURITY_APPLY] Delay entre as execuções.''
    ';
  END IF;

END;
$$
LANGUAGE plpgsql;
