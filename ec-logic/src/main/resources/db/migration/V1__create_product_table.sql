CREATE TABLE IF NOT EXISTS products (
                                        id VARCHAR(50) NOT NULL,
    name VARCHAR(255) NULL,
    sales_start_period DATETIME(6) NULL,
    sales_end_period DATETIME(6) NULL,
    status VARCHAR(50) NULL,
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
    )