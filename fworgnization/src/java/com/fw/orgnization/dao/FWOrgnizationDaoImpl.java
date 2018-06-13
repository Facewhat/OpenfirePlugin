package com.fw.orgnization.dao;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.util.JiveConstants;
import org.jivesoftware.util.StringUtils;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

import com.csvreader.CsvWriter;
import com.fw.orgnization.entity.FWGroup;
import com.fw.orgnization.entity.FWGroupMessageHistory;
import com.fw.orgnization.entity.FWGroupUser;

public class FWOrgnizationDaoImpl implements FWOrgnizationDao {
	
	
	public static final String SELECT_FWGROUPUSER_BYDEPARTMENT = "SELECT groupname, username, usernickname, fullpinyin, shortpinyin FROM fwgroupuser WHERE groupname = ?";
	public static final String SELECT_ALL_FWGROUPUSER = "SELECT groupname, username, usernickname, fullpinyin, shortpinyin FROM fwgroupuser";
	public static final String INSERT_OFFLINE =
	        "INSERT INTO ofOffline (username, messageID, creationDate, messageSize, stanza) " +
	        "VALUES (?, ?, ?, ?, ?)";
	public static final String INSERT_HISTORY_MESSAGE = "INSERT INTO fwGroupMessageHistory(groupname, username, sentDate, body) values (?, ?, ?, ?)";
	public static final String FIND_USERS_BY_GROUP_DISNAME = "select groupname, username, usernickname, fullpinyin, shortpinyin from fwgroupuser where groupname = (select fwgroup.groupname from fwgroup where displayname = ? )";
	
	private static final String SQL_REVISEDEPARTMENTUSERGROUPNAME = "update fwgroupuser set groupname = ? where groupname = ?";
	private static final String SQL_ADDUSER = "insert into fwgroupuser(groupname,username,usernickname,fullpinyin,shortpinyin) values(? , ? , ? , ? , ?)  ";
	private static final String SQL_DELETEUSERDATA = "delete from fwgroup";
	private static final String SQL_DELETEGROUPDATA = "delete from fwgroupuser";
	private static final String SQL_GETALLDEPARTMENTSINFO = "select groupname, displayname, groupfathername, creationdate, isorgnization FROM fwgroup WHERE isorgnization = 1";
	private static final String SQL_DOWNLOADCSV = "select displayname,fwgroup.groupname,usernickname,username,fullpinyin from " + 
			"fwgroup left join fwgroupuser on fwgroup.groupname = fwgroupuser.groupname";
	private static final String SQL_GROUPORUSERSEARCH = "select fwgroup.groupname,displayname,username,usernickname,fullpinyin,shortpinyin from "
			+ "fwgroup left join fwgroupuser on fwgroup.groupname = fwgroupuser.groupname where 1=1 and (fwgroup.displayname like ?) or (fwgroup.groupname like ?) "
			+ "or (fwgroupuser.username like ?) or (fwgroupuser.usernickname like ?) or (fwgroupuser.fullpinyin like ?) or (fwgroupuser.shortpinyin like ?)";
	private static final String SQL_DELETEUSER = "delete from fwgroupuser where usernickname = ?";
	private static final String SQL_DELETEUSERBYGROUPDISPLAYNAME = "delete from fwgroupuser  where usernickname= ? and groupname = (select groupname from fwgroup where displayname = ?)";
	private static final String SQL_MOVEUSER = "update fwgroupuser set groupname = ? where usernickname = ?";
	private static final String SQL_GETUSERINFOBYGROUPNAME = "select * from fwgroupuser where groupname = ? and usernickname = ?";
	private static final String SQL_GETDEPARTMENTSINFO = "select * from fwgroup where groupfathername = ?";
	private static final String SQL_GETUSERINFO = "select * from fwgroupuser where usernickname = ? ";
	private static final String SQL_REVISEUSER = "update fwgroupuser  set username = ? ,usernickname = ?,fullpinyin = ?, shortpinyin = ? where usernickname = ?";
	private static final String SQL_DELETEDEPARTMENTUSERS = "delete from fwgroupuser where groupname = ?";
	private static final String SQL_DELETEDEPARTMENT = "delete from fwgroup where displayname = ?";
	private static final String SQL_GETDEPARTMENTINFO = "select * from fwgroup where displayname = ?";
	private static final String SQL_GETGROUPFATHERID = "select groupfathername from fwgroup where displayname = ?";
	private static final String SQL_ADDDEPARTMENT = "insert into fwgroup(groupname,displayname,groupfathername,isorgnization) values(? , ? , ? , ?)";
	private static final String SQL_REVISEDEPARTMENT = "update fwgroup set groupname = ? ,displayname = ? where displayname = ?";
	
	@Override
	public void saveMessage(FWGroupMessageHistory message) throws Exception {
		// TODO 自动生成的方法存根
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(INSERT_HISTORY_MESSAGE);
			pstmt.setString(1, message.getGroupname());
			pstmt.setString(2, message.getUsername());
			pstmt.setString(3, String.valueOf(message.getSendtDate().getTime()));
			pstmt.setString(4, message.getBody());
			pstmt.execute();
		} catch (Exception e) {
			throw e;
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
	}

	@Override
	public void addMessage(Message message) {
		// TODO 自动生成的方法存根
		if (message == null) {
            return;
        }
		JID recipient = message.getTo();
		String username = recipient.getNode();
		// 用户名为空，（例如匿名用户）不存储
		if (username == null || !UserManager.getInstance().isRegisteredUser(recipient)) {
            return;
        } else if (!XMPPServer.getInstance().getServerInfo().getXMPPDomain().equals(recipient.getDomain())) {
            // Do not store messages sent to users of remote servers
        	// 不保存发送给远程服务器的用户
            return;
        }
		// 获得消息的xml格式
		String msgXML = message.getElement().asXML();
		long messageID = SequenceManager.nextID(JiveConstants.OFFLINE);
		
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
        	System.out.println("给 " + message.getFrom().toString() + "保存离线消息 ");
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(INSERT_OFFLINE);
            pstmt.setString(1, username);
            pstmt.setLong(2, messageID);
            pstmt.setString(3, StringUtils.dateToMillis(new java.util.Date()));
            pstmt.setInt(4, msgXML.length());
            pstmt.setString(5, msgXML);
            pstmt.executeUpdate();
        } catch (Exception e) {
           System.out.println("保存消息出错，" + e.getMessage());
        } finally {
            DbConnectionManager.closeConnection(pstmt, con);
        }
	}

	

	@Override
	public List<FWGroupUser> getDepartmentUser(String groupname)
			throws Exception {
		List<FWGroupUser> list = new ArrayList<FWGroupUser>();
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SELECT_FWGROUPUSER_BYDEPARTMENT);
            pstmt.setString(1, groupname);
            rs = pstmt.executeQuery();
            while (rs.next()){
                // conversations.add(extractConversation(rs));
               //  ArchivedMessage archivedMessage = new ArchivedMessage(time, direction, type);
            	list.add(extractFWGroupUser(rs));
            }
        }
        catch (SQLException sqle) {
           
            sqle.printStackTrace();
        }
        finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
        
		return list;
	}

	@Override
	public List<FWGroupUser> getAllGroupUser() throws Exception {
		// TODO 自动生成的方法存根
		List<FWGroupUser> list = new ArrayList<FWGroupUser>();
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SELECT_ALL_FWGROUPUSER);
            rs = pstmt.executeQuery();
            while (rs.next()){
                // conversations.add(extractConversation(rs));
               //  ArchivedMessage archivedMessage = new ArchivedMessage(time, direction, type);
            	list.add(extractFWGroupUser(rs));
            }
        }
        catch (SQLException sqle) {
            
            sqle.printStackTrace();
        }
        finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
		return list;
	}
	
	private FWGroupUser extractFWGroupUser(ResultSet rs) throws SQLException {
		return new FWGroupUser(
				rs.getString(1), 
				rs.getString(2), 
				rs.getString(3), 
				rs.getString(4), 
				rs.getString(5));
	}
	
	private FWGroup extractFWGroup(ResultSet rs) throws SQLException {
		FWGroup fwGroup = new FWGroup(rs.getString(1),
				rs.getString(2), 
				rs.getString(3), 
				rs.getString(4));
		int isOrgnization = rs.getInt(5);
		if(isOrgnization == 1) {
			fwGroup.setIsorgnization(true); 
		} else {
			fwGroup.setIsorgnization(false); 
		}
		return fwGroup;
	}

	@Override
	public List<FWGroupUser> getUsersByDepartmentDisName(String disName)
			{
		// TODO 自动生成的方法存根
		List<FWGroupUser> list = new ArrayList<FWGroupUser>();
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(FIND_USERS_BY_GROUP_DISNAME);
            pstmt.setString(1, disName);
            rs = pstmt.executeQuery();
            while (rs.next()){
            	list.add(extractFWGroupUser(rs));
            }
        }
        catch (SQLException sqle) {
           
            sqle.printStackTrace();
        }
        finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
        
		return list;
	}

	@Override
	public String getGroupFatherId(String groupdisplayName) {
		// TODO 自动生成的方法存根
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String groupId = null;
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_GETGROUPFATHERID);
			pstmt.setString(1, groupdisplayName);
			rs = pstmt.executeQuery();
			if(rs.next()){
				//System.out.println(rs.getString(1));
				groupId = rs.getString(1);
			}
			
        } catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
        
		return groupId;
	}




	@Override
	public void addDepartment(FWGroup fwGroup) throws Exception {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_ADDDEPARTMENT);
			pstmt.setString(1, fwGroup.getGroupname());
			pstmt.setString(2, fwGroup.getDisplayname());
			pstmt.setString(3, fwGroup.getGroupfathername());
			pstmt.setString(4,"1");
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			throw e;
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
	}

	@Override
	public void addUser(FWGroupUser fwGroupUser) throws Exception {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_ADDUSER);
			pstmt.setString(1, fwGroupUser.getGroupname());
			pstmt.setString(2, fwGroupUser.getUsername());
			pstmt.setString(3, fwGroupUser.getUsernickname());
			pstmt.setString(4, fwGroupUser.getFullpinyin());
			pstmt.setString(5, fwGroupUser.getUsername());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			throw e;
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
	}
	
	@Override
	public void reviseDepartment(FWGroup fwGroup,String groupDisplayName) throws Exception {
		// TODO Auto-generated method stub
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_REVISEDEPARTMENT);
			pstmt.setString(1, fwGroup.getGroupname());
			pstmt.setString(2, fwGroup.getDisplayname());
			pstmt.setString(3, groupDisplayName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			throw e;
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
	}

	@Override
	public FWGroup getDepartmentInfo(String groupDisplayName) {
		// TODO Auto-generated method stub
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        FWGroup fwGroup = null; 
        try {
			con = DbConnectionManager.getConnection();
			pstmt =  con.prepareStatement(SQL_GETDEPARTMENTINFO);
			pstmt.setString(1, groupDisplayName);
			rs = pstmt.executeQuery();
			if(rs.next()){
				fwGroup = new FWGroup();
				fwGroup.setGroupname(rs.getString(1));
				fwGroup.setDisplayname(rs.getString(2));
			}
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
		return fwGroup;
	
	}

	@Override
	public void deleteDepartment(String groupDisplayName) {
		// TODO Auto-generated method stub
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
			con = DbConnectionManager.getConnection();
			pstmt =  con.prepareStatement(SQL_DELETEDEPARTMENT);
			pstmt.setString(1, groupDisplayName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
		
	}

	@Override
	public void deleteDepartmentUsers(String groupname) {
		// TODO Auto-generated method stub
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
			con = DbConnectionManager.getConnection();
			pstmt =  con.prepareStatement(SQL_DELETEDEPARTMENTUSERS);
			pstmt.setString(1, groupname);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
	}
	
	@Override
	public String reviseUser(FWGroupUser fwGroupUser,String userNickName) {
		// TODO 自动生成的方法存根
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_REVISEUSER);
			pstmt.setString(1, fwGroupUser.getUsername());
			pstmt.setString(2, fwGroupUser.getUsernickname());
			pstmt.setString(3, fwGroupUser.getFullpinyin());
			pstmt.setString(4, fwGroupUser.getShortpinyin());
			pstmt.setString(5, userNickName);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			//return "error";
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
		return "success";
	}
	
	@Override
	public FWGroupUser getUserInfo(String userNickName) {
		// TODO 自动生成的方法存根
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        FWGroupUser fwGroupUser = null; 
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_GETUSERINFO);
			pstmt.setString(1, userNickName);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				fwGroupUser = new FWGroupUser();
				fwGroupUser.setGroupname(rs.getString(1));
				fwGroupUser.setUsername(rs.getString(2));
				fwGroupUser.setUsernickname(rs.getString(3));
				fwGroupUser.setFullpinyin(rs.getString(4));
				fwGroupUser.setShortpinyin(rs.getString(5));
			}
				
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
		
		return fwGroupUser;
	}

	@Override
	public List<FWGroup> getDepartmentsInfo(String treeNodeLevel) {
		// TODO Auto-generated method stub
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<FWGroup> groupList = null;
      
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_GETDEPARTMENTSINFO);
			pstmt.setString(1, treeNodeLevel);
			rs = pstmt.executeQuery();
			groupList = new ArrayList<FWGroup>();
			while(rs.next()) {
				groupList.add(extractFWGroup(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
		return groupList;
		
	}

	@Override
	public FWGroupUser getUserInfo(String groupName, String userNickName) {
		// TODO Auto-generated method stub
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        FWGroupUser fwGroupUser = null;
        
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_GETUSERINFOBYGROUPNAME);
			pstmt.setString(1, groupName);
			pstmt.setString(2, userNickName);
			rs = pstmt.executeQuery();
			if(rs.next()) 
			{
				fwGroupUser = extractFWGroupUser(rs);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
		return fwGroupUser;
	}

	@Override
	public void moveUser(String toGroupDisplayName, String userNickName) {
		// TODO Auto-generated method stub
		Connection con = null;
        PreparedStatement pstmt = null;
        
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_MOVEUSER);
			pstmt.setString(1, toGroupDisplayName);
			pstmt.setString(2, userNickName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
	}

	@Override
	public void deleteUser(String groupDisplayName, String userNickName) {
		// TODO Auto-generated method stub
		Connection con = null;
        PreparedStatement pstmt = null;
        
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_DELETEUSERBYGROUPDISPLAYNAME);
			pstmt.setString(1, userNickName);
			pstmt.setString(2, groupDisplayName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
		
	}

	@Override
	public void deleteUser(String userNickName) {
		// TODO Auto-generated method stub
		Connection con = null;
        PreparedStatement pstmt = null;
        
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_DELETEUSER);
			pstmt.setString(1, userNickName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
	}
	
	@Override
	public List<Map<String, String>> searchUserOrGroup(String searchCondition) {
		// TODO Auto-generated method stub
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Map<String, String>> groupAndUserInfo = null;
        try {
			 con = DbConnectionManager.getConnection();
			 pstmt = con.prepareStatement(SQL_GROUPORUSERSEARCH);
			 pstmt.setString(1, "%" + searchCondition + "%");
			 pstmt.setString(2, "%" + searchCondition + "%");
			 pstmt.setString(3, "%" + searchCondition + "%");
			 pstmt.setString(4, "%" + searchCondition + "%");
			 pstmt.setString(5, "%" + searchCondition + "%");
			 pstmt.setString(6, "%" + searchCondition + "%");
			 rs = pstmt.executeQuery();
			 groupAndUserInfo  = new ArrayList<Map<String, String>>();
			 int id = 0;
			 
			 while(rs.next()) {
				 id++;
				 groupAndUserInfo.add(groupuAndUerInfoListDeal(rs,id));
			 }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
        return groupAndUserInfo;
	}
	

	
	public Map<String, String> groupuAndUerInfoListDeal(ResultSet rs,int id) {
		
		Map<String, String> groupAndUserMap = null;
		groupAndUserMap = new HashMap<String,String>();
		try {
			groupAndUserMap.put("groupName", rs.getString(1));
			groupAndUserMap.put("groupDisplayName", rs.getString(2));
			groupAndUserMap.put("userName", rs.getString(3));
			groupAndUserMap.put("userNickName",rs.getString(4));
			groupAndUserMap.put("fullPinYin",rs.getString(5));
			groupAndUserMap.put("shortPinYin",rs.getString(6));
			groupAndUserMap.put("action","" + id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groupAndUserMap;
	}
	// groupuAndUerInfoListDeal
	
    private File createEmptyFile(String filename) throws Exception {  
        
        File file = new File(filename);  
        try {  
            if (file.exists()) {      
                file.delete();  
                file.createNewFile();  
            } else {  
                file.createNewFile();  
            }  
        }catch (IOException e) {  
            e.printStackTrace();  
            throw new Exception(e.getMessage());  
        }  
        return file;  
    }
    
	public String downloadCSV() {
		String filePath = "D://downloadCSVFile.csv";
		File temp = null;
		try {
			temp = createEmptyFile(filePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CsvWriter csvWriter = null;
		try {
			csvWriter = new CsvWriter(filePath,',', Charset.forName("utf-8"));
			String[] headers = {"部门","部门简拼","成员","成员简拼","成员全拼"};
			csvWriter.writeRecord(headers);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_DOWNLOADCSV);
			rs = pstmt.executeQuery();
			
			int i = 0;
			StringBuffer stringBuffer = new StringBuffer();
			
			while (rs.next()) {
			   
			    for (int j = 1; j<=5; j++){

			        String value = rs.getString(j);
			        //创建列
			        stringBuffer.append(value);
			        if (j != 5){
			            stringBuffer.append(',');
			        }
			    }

			    String buffer_string = stringBuffer.toString();
			    String[] content = buffer_string.split(",");
			    try {
					csvWriter.writeRecord(content);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    stringBuffer.setLength(0);
			}
			try {
				csvWriter.flush();
				csvWriter.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return filePath;
	}
	
	@Override
	public List<FWGroup> getAllDepartment() throws Exception {
		// TODO 自动生成的方法存根
		List<FWGroup> list = null;
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
        	
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SQL_GETALLDEPARTMENTSINFO);
            rs = pstmt.executeQuery();
            list =  new ArrayList<FWGroup>();
            while (rs.next()){
               
            	list.add(extractFWGroup(rs));
            }
        }
        catch (SQLException sqle) {
            
            sqle.printStackTrace();
        }
        finally {
            DbConnectionManager.closeConnection(rs, pstmt, con);
        }
        
		return list;
	}

	@Override
	public void deleteUserSQLData() {
		// TODO Auto-generated method stub
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_DELETEUSERDATA);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
	}

	@Override
	public void deleteGroupSQLData() {
		// TODO Auto-generated method stub
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_DELETEGROUPDATA);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
	}

	@Override
	public void reviseDepartmentUserGroupName(String oldGroupName, String newGroupName) {
		// TODO Auto-generated method stub
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_REVISEDEPARTMENTUSERGROUPNAME);
			pstmt.setString(1, newGroupName);
			pstmt.setString(2, oldGroupName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
		
	}
}
