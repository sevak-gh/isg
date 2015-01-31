package com.infotech.isg.repository.jdbc;

import com.infotech.isg.repository.AuditLogRepository;

import java.util.Date;
import javax.sql.DataSource;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * jdbc implementation for audit log repository.
 *
 * @author Sevak Gharibian
 */
@Repository("JdbcAuditLogRepository")
public class JdbcAuditLogRepositoryImpl implements AuditLogRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcAuditLogRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void create(String username, String bankCode, int amount,
                       String channel, String state, String bankReceipt,
                       String orderId, String consumer, String customerIp,
                       String remoteIp, String action, int operatorId,
                       String status, long isgDoc, String oprDoc,
                       Date timestamp, long responseTime) {

        final String sql = "insert into info_topup_audit(username, bankCode, amount, channel, state, bankReceipt, orderId, consumer, "
                           + "customerIp, remoteIp, action, operator, status, isgDoc, oprDoc, timestamp, responseTime) values("
                           + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql, new Object[] {username, bankCode, amount, channel, state, bankReceipt,
                                               orderId, consumer, customerIp, remoteIp, action, operatorId,
                                               status, isgDoc, oprDoc, timestamp, responseTime
                                              });
    }
}

