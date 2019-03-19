package net.komus;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class AppSNMP {
	
	public static void main( String[] args ) throws IOException {
		
		PDU trap = new PDU();
		trap.setType( PDU.TRAP );
		
		OID oid = new OID( ".1.3.6.1.4..1.77.1.2.25.1.1.5.65.68.77.73.78" );
		trap.add( new VariableBinding( SnmpConstants.snmpTrapOID, oid ) );
		trap.add( new VariableBinding( SnmpConstants.sysUpTime, new TimeTicks( 10000 ) ) ); // put your uptime here
		//trap.add( new VariableBinding( SnmpConstants.sysDescr, new OctetString( "Link is down - authenticationFailure" ) ) );

		Variable var = new OctetString( "Minor" );
		trap.add( new VariableBinding( oid, var ) );
		trap.setType( PDU.NOTIFICATION );
		
		// Specify receiver
	    Address targetaddress = new UdpAddress( "172.19.99.46/162" );
	    CommunityTarget target = new CommunityTarget();
	    target.setCommunity( new OctetString("public") );
	    target.setVersion( SnmpConstants.version2c );
	    target.setAddress( targetaddress );
	    
	    // Send
	    Snmp snmp = new Snmp( new DefaultUdpTransportMapping() );
	    snmp.send(trap, target, null, null);
	    System.out.println( "send snmp trap: done..." );
	}
	
}
