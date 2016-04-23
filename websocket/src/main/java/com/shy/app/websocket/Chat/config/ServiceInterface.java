package com.shy.app.websocket.Chat.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public interface ServiceInterface<T> {
	Connection getConnection();
	List<?> findAll();
	List<?> find(int id);
	int save(T model);
	void close(ResultSet resultSet, PreparedStatement pstmt, Connection connection);
}
