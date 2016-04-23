package com.shy.app.websocket.Chat.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.shy.app.websocket.Items;

public class ItemService {
	String jdbc_driver = "com.mysql.jdbc.Driver"; 
	String db_url = "jdbc:mysql://localhost/realtime";
	String user = "root";
	String pass = "";
	
	public List<Items> getAllItems() {
    	List<Items> itemList = new ArrayList<>();
    	
    	Connection connection = null;
    	PreparedStatement pstmt = null;
    	
    	String sql = "SELECT id, name, description FROM items";
    	
    	try  {
    		Class.forName(jdbc_driver);
			connection = DriverManager.getConnection(db_url, user, pass);
			
    		pstmt = connection.prepareStatement(sql);
    		ResultSet rs = pstmt.executeQuery();
    		while (rs.next()) {
            	int id  = rs.getInt("id");
    			String name = rs.getString("name");
    			String description = rs.getString("description");
    			Items item = new Items();
    			item.setId(id);
    			item.setName(name);
    			item.setDescription(description);
    			itemList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        	if(pstmt != null)  {
        		try {
        			pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	}
        	
        	if(connection != null) {
        		try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	}
        }
    	
       return itemList;
    }
	
	public int saveItem(Items item) throws SQLException {
		int saveStatus = 0;
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try {
			connection = getConnection();
			
			String sql = "";
			
			if(item.getId() != 0 && item.getId() > 0) { //update
				sql = "UPDATE items SET name = ?, description = ? WHERE id = ?";
				
			}else  { //insert
				sql = "INSERT INTO items(name, description) VALUES(?,?)";
			}
			
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, item.getName());
			preparedStatement.setString(2, item.getDescription()); 
			preparedStatement.executeUpdate();
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(connection != null)
				connection.close();
			if(preparedStatement != null)
				preparedStatement.close();
		}
		
		return saveStatus;
	}
	
	public Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName(jdbc_driver);
			connection = DriverManager.getConnection(db_url, user, pass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	
}
