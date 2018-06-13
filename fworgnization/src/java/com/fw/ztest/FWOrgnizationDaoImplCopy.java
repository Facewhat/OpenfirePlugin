package com.fw.ztest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.database.SequenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.util.JiveConstants;
import org.jivesoftware.util.StringUtils;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

import com.fw.orgnization.dao.FWOrgnizationDao;
import com.fw.orgnization.entity.FWGroup;
import com.fw.orgnization.entity.FWGroupMessageHistory;
import com.fw.orgnization.entity.FWGroupUser;

public class FWOrgnizationDaoImplCopy implements FWOrgnizationDaoCopy {
	
	public static final String SELECT_ALL_FWGROUP_DEPTARTMENT = "SELECT groupname, displayname, groupfathername, creationdate, isorgnization FROM fwgroup WHERE isorgnization = 1";
	public static final String SELECT_FWGROUPUSER_BYDEPARTMENT = "SELECT groupname, username, usernickname, fullpinyin, shortpinyin FROM fwgroupuser WHERE groupname = ?";
	public static final String SELECT_ALL_FWGROUPUSER = "SELECT groupname, username, usernickname, fullpinyin, shortpinyin FROM fwgroupuser";
	public static final String INSERT_OFFLINE =
	        "INSERT INTO ofOffline (username, messageID, creationDate, messageSize, stanza) " +
	        "VALUES (?, ?, ?, ?, ?)";
	public static final String INSERT_HISTORY_MESSAGE = "INSERT INTO fwGroupMessageHistory(groupname, username, sentDate, body) values (?, ?, ?, ?)";
	public static final String FIND_USERS_BY_GROUP_DISNAME = "select groupname, username, usernickname, fullpinyin, shortpinyin from fwgroupuser where groupname = (select fwgroup.groupname from fwgroup where displayname = ? )";
	
	public static final String SQL_GET_GROUPFATHERID_BY_DISNAME = "select groupfathername from fwgroup where displayname = ?";
	
	public static final String SQL_GET_USER_INFO_BY_USERNICKNAME = "select * from fwgroupuser where usernickname = ? ";
	public static final String SQL_UPDATE_USERINFO_BY_DISNAME = "update fwgroupuser  set username = ? ,usernickname = ?,fullpinyin = ?, shortpinyin = ? where usernickname = ?";
	private static final String SQL_GET_GROUP_BY_DISGROUPNAME = "select * from fwgroup where displayname = ?";
	private static final String SQL_DELETE_USER_FROM_GROUP_BY_USERNICKNAME = "delete from fwgroupuser where usernickname = ?";
	private static final String SQL_REVISE_GROUPINFO_BY_GROUPDISNAME = "update fwgroup set groupname = ? ,displayname = ? where displayname = ?";
	private static final String SQL_REVISE_GROUPNAME_OF_USER_BY_GROUPNAME = "update fwgroupuser set groupname =? where groupname = ?";
	private static final String SQL_ADDDEPARTMENT = "insert into fwgroup(groupname,displayname,groupfathername,isornization) values (? , ? , ? , ?)";
	
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
	public List<FWGroup> getAllDepartment() throws Exception {
		// TODO 自动生成的方法存根
		List<FWGroup> list = new ArrayList<FWGroup>();
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DbConnectionManager.getConnection();
            pstmt = con.prepareStatement(SELECT_ALL_FWGROUP_DEPTARTMENT);
            rs = pstmt.executeQuery();
            while (rs.next()){
                // conversations.add(extractConversation(rs));
               //  ArchivedMessage archivedMessage = new ArchivedMessage(time, direction, type);
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
			pstmt = con.prepareStatement(SQL_GET_GROUPFATHERID_BY_DISNAME);
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
	public FWGroupUser getUserInfoByDisName(String disName) {
		// TODO 自动生成的方法存根
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        FWGroupUser fwGroupUser = null; 
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_GET_USER_INFO_BY_USERNICKNAME);
			pstmt.setString(1, disName);
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
	public String reviseUserInfo(FWGroupUser fwGroupUser,String disName) {
		// TODO 自动生成的方法存根
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(SQL_UPDATE_USERINFO_BY_DISNAME);
			pstmt.setString(1, fwGroupUser.getUsername());
			pstmt.setString(2, fwGroupUser.getUsernickname());
			pstmt.setString(3, fwGroupUser.getFullpinyin());
			pstmt.setString(4, fwGroupUser.getShortpinyin());
			pstmt.setString(5, disName);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return "error";
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
		return "success";
	}

	@Override
	public FWGroup getDepartmentInfoByDisGroupName(String disGroupName) {
		// TODO 自动生成的方法存根
		Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        FWGroup fwGroup = null; 
        try {
			con = DbConnectionManager.getConnection();
			pstmt =  con.prepareStatement(SQL_GET_GROUP_BY_DISGROUPNAME);
			pstmt.setString(1, disGroupName);
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
	public void deleteUserByUserNickName(String userNickName) {
		// TODO 自动生成的方法存根
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
			con = DbConnectionManager.getConnection();
			pstmt =  con.prepareStatement(SQL_DELETE_USER_FROM_GROUP_BY_USERNICKNAME);
			pstmt.setString(1, userNickName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
      
	}

	@Override
	public String reviseGroupInfoByDisName(FWGroup fwGroup, String disName) {
		// TODO 自动生成的方法存根
		Connection con = null;
        PreparedStatement pstmt = null;
        String oldGroupName = getDepartmentInfoByDisGroupName(disName).getGroupname();
        try {
			con = DbConnectionManager.getConnection();
			pstmt =  con.prepareStatement(SQL_REVISE_GROUPINFO_BY_GROUPDISNAME);
			pstmt.setString(1, fwGroup.getGroupname());
			pstmt.setString(2, fwGroup.getDisplayname());
			pstmt.setString(3, disName);
			pstmt.executeUpdate();
			reviseGroupNameOfUserByGroupName(fwGroup.getGroupname(),oldGroupName);
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return "error";
		}
        return "success";
	}
	
	public void reviseGroupNameOfUserByGroupName(String groupName,String disName){
		Connection con = null;
        PreparedStatement pstmt = null;
        try {
			con = DbConnectionManager.getConnection();
			pstmt =  con.prepareStatement(SQL_REVISE_GROUPNAME_OF_USER_BY_GROUPNAME);
			pstmt.setString(1, groupName);
			pstmt.setString(2, disName);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}finally {
            DbConnectionManager.closeConnection( pstmt, con);
        }
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
			pstmt.setBoolean(4, fwGroup.isIsorgnization());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			throw e;
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
		}
	}


}
