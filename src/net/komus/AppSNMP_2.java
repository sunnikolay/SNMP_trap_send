package net.komus;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class AppSNMP_2 {
	
	private static final String community = "public";
	private static final String trapOid = ".1.3.6.1.2.1.1.6";
	private static final String ipAddress = "172.19.99.46";
	private static final int port = 162;

	public static void main( String args[] ) {
		sendSnmpTrap( SnmpConstants.version2c );	  
	}

    /**
     * This methods sends the V1/V2 trap
     * 
     * @param version
     */
	private static void sendSnmpTrap( int version ) {
		sendTrap(version, community, ipAddress, port);
	}

	private static PDU createPdu( int snmpVersion ) {
		PDU pdu = new PDU();
		pdu.setType( PDU.TRAP );
		pdu.setRequestID( new Integer32( 123 ) );
	    	   
	    pdu.add( new VariableBinding( SnmpConstants.sysUpTime) );
	    pdu.add( new VariableBinding( SnmpConstants.snmpTrapOID, new OID( trapOid ) ) );
	    pdu.add( new VariableBinding( SnmpConstants.snmpTrapAddress, new IpAddress( ipAddress ) ) );
	    pdu.add( new VariableBinding( SnmpConstants.sysDescr, new OctetString( "Link is down - authenticationFailure" ) ) );
	    pdu.add( new VariableBinding( new OID( trapOid ), new OctetString( "Major" ) ) );
	    return pdu;
	}

	private static void sendTrap(int snmpVersion, String community, String ipAddress, int port) {
		try {
			PDU snmpPDU = createPdu( snmpVersion );

			// Create Transport Mapping
			TransportMapping<?> transport = new DefaultUdpTransportMapping();
	    
			// Create Target
			CommunityTarget comtarget = new CommunityTarget();
			comtarget.setCommunity( new OctetString( community ) );
			comtarget.setVersion( snmpVersion );
			comtarget.setAddress( new UdpAddress( ipAddress + "/" + port ) );
			comtarget.setRetries( 2 );
			comtarget.setTimeout( 5000 );

			// Send the PDU
			Snmp snmp = new Snmp( transport );
			snmp.send( snmpPDU, comtarget );
			System.out.println( "Sent Trap to (IP:Port)=> " + ipAddress + ":" + port );
			snmp.close();
	    } 
		catch (Exception e) {
			System.err.println( "Error in Sending Trap to (IP:Port)=> " + ipAddress + ":" + port);
			System.err.println( "Exception Message = " + e.getMessage() );
	    }
	}
  
}
