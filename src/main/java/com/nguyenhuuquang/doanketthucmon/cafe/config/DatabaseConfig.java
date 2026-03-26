package com.nguyenhuuquang.doanketthucmon.cafe.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        String rawUrl = System.getenv("DATABASE_URL");
        String jdbcUrl;

        if (rawUrl != null && rawUrl.startsWith("postgresql://")) {
            String withoutScheme = rawUrl.substring("postgresql://".length());
            String hostAndDb = withoutScheme.substring(withoutScheme.indexOf("@") + 1);
            jdbcUrl = "jdbc:postgresql://" + hostAndDb;
        } else if (rawUrl != null && rawUrl.startsWith("jdbc:postgresql://")) {
            jdbcUrl = rawUrl;
        } else {
            jdbcUrl = "jdbc:postgresql://localhost:5432/doanketthucmon";
        }

        String username = System.getenv("PGUSER");
        String password = System.getenv("PGPASSWORD");

        if ((username == null || password == null) && rawUrl != null && rawUrl.contains("@")) {
            String withoutScheme = rawUrl.substring(rawUrl.indexOf("://") + 3);
            String credentials = withoutScheme.substring(0, withoutScheme.indexOf("@"));
            if (credentials.contains(":")) {
                username = credentials.split(":", 2)[0];
                password = credentials.split(":", 2)[1];
            }
        }

        if (username == null)
            username = "postgres";
        if (password == null)
            password = "postgres";

        System.out.println("=== DB Connection ===");
        System.out.println("JDBC URL: " + jdbcUrl);
        System.out.println("Username: " + username);
        System.out.println("====================");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setInitializationFailTimeout(-1);
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        config.setInitializationFailTimeout(60000);

        return new HikariDataSource(config);
    }
}