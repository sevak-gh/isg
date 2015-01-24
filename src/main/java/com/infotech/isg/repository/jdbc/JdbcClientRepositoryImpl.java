package com.infotech.isg.repository.jdbc;

import com.infotech.isg.domain.Client;
import com.infotech.isg.repository.ClientRepository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * jdbc implementation for Client repository.
 *
 * @author Sevak Gharibian
 */
@Repository("JdbcClientRepository")
public class JdbcClientRepositoryImpl implements ClientRepository {

    private final Logger LOG = LoggerFactory.getLogger(JdbcClientRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcClientRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Client findByUsername(String username) {
        Client client = null;
        String sql = "select id, client, pin, active, vendor from info_topup_clients where client = ?";
        try {
            client = jdbcTemplate.queryForObject(sql, new Object[] {username}, new ClientRowMapper());
        } catch (EmptyResultDataAccessException e) {
            LOG.debug("jdbctemplate empty result set handled", e);
            return null;
        }

        sql = "select ip from info_topup_client_ips where client = ?";
        List<String> ips = jdbcTemplate.queryForList(sql, new Object[] {client.getId()}, String.class);
        for (String ip : ips) {
            client.addIp(ip);
        }

        return client;
    }

    private static final class ClientRowMapper implements RowMapper<Client> {
        @Override
        public Client mapRow(ResultSet rs, int rowNum) throws SQLException {
            Client client = new Client();
            client.setId(rs.getInt("id"));
            client.setUsername(rs.getString("client"));
            client.setPassword(rs.getString("pin"));
            client.setIsActive(((rs.getString("active").equalsIgnoreCase("Y")) ? true : false));
            client.setVendor(rs.getString("vendor"));
            return client;
        }
    }
}
