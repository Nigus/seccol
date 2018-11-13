package com.portsip.util;
/**
 * Property of IT Man AS, Bryne Norway It is strictly forbidden to copy or modify the authors file,
 * (c) 2015 IT Man AS
 * Created by Nigussie on 26.03.2015.
 */
import com.portsip.PortSipEnumDefine;

public class UserInfo {
	String mUsername = ""; //phone number to communicate with the asterisk pbx
	String mUserpwd= ""; // secret password for aster
	static String mSipServer= "@string/sip_server";//callman.cloudapp.net
	String mUserdomain= "";//191.237.216.242
	String mAuthName= "";
	String mUserDisName= ""; //Real name
	String mAddress="";
	static String mStunSvr= "@string/sip_server"; //callman.cloudapp.net
	int mStunPort= 3478;
	int msipPort= 5060;
	int mtransType= PortSipEnumDefine.ENUM_TRANSPORT_UDP;	

	public void setUserName(String userName){
		mUsername =userName;
	}
	
	public String getUserName() {
		return mUsername;
	}
	
	public void setUserPwd(String password){
		mUserpwd =password;
	}
	
	public String getUserPassword() {
		return mUserpwd;
	}
	
	public void setSipServer(String server){
		mSipServer =server;
	}
	
	public String getSipServer() {
		return mSipServer;
	}
	
	public void setUserDisplayName(String dispalyName){
		mUserDisName =dispalyName;
	}
	
	public String getUserDisplayName() {
		return mUserDisName;
	}
	
	public void setUserDomain(String domain){
		mUserdomain =domain;
	}
	public String getUserdomain() {
		return mUserdomain;
	}
	
	public void setAuthName(String authName){
		mAuthName = authName;
	}
	public String getAuthName() {
		return mAuthName;
	}
	
	public void setSipPort(int port){
		msipPort = port;
	}
	
	public int getSipPort(){
		return msipPort;
	}
	
	public void setStunServer(String stunServer){
		mStunSvr = stunServer;
	}
	
	public String getStunServer(){
		return mStunSvr;
	}
	
	public void setStunPort(int port){
		mStunPort = port;
	}
	
	public int getStunPort(){
		return mStunPort;
	}
	
	public void setTranType(int enum_transType){
		mtransType = enum_transType;
	}
	
	public int getTransType(){
		return mtransType;
	}
	public boolean isAvailable(){
		
		if (mUsername != null && mUsername.length() > 0 && 
				mUserpwd!= null	&& mUserpwd.length() > 0 &&
				msipPort>0&&msipPort<65535&& 
				mSipServer!= null&&mSipServer.length() > 0)// these fields are required
		{
			return true;
		}
		return false;
	}

}
