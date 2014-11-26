package com.infotech.isg.repository.jdbc;

import com.infotech.isg.domain.PaymentChannel;
import com.infotech.isg.repository.PaymentChannelRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.dao.EmptyResultDataAccessException;


/**
* jdbc implementation for PaymentChannel repository.
*
* @author Sevak Gharibian
*/
@Repository("JdbcPaymentChannelRepository")
public class JdbcPaymentChannelRepositoryImpl implements PaymentChannelRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcPaymentChannelRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public PaymentChannel findById(String id) {
        PaymentChannel paymentChannel = null;
        String sql = "select channel, active info_topup_payment_channel where channel = ?";
        try {
            paymentChannel = jdbcTemplate.queryForObject(sql, new Object[] {id}, new PaymentChannelRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return paymentChannel;
    }

    private static final class PaymentChannelRowMapper implements RowMapper<PaymentChannel> {
        @Override
        public PaymentChannel mapRow(ResultSet rs, int rowNum) throws SQLException {
            PaymentChannel paymentChannel = new PaymentChannel();
            paymentChannel.setId(rs.getString("channel"));
            paymentChannel.setIsActive(((rs.getString("active").compareToIgnoreCase("Y") == 0) ? true : false));
            return paymentChannel;
        }
    }
}
