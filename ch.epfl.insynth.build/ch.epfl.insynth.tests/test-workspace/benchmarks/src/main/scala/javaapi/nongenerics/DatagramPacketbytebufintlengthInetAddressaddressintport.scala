package javaapi.DatagramPacketbytebufintlengthInetAddressaddressintport

//http://www.java2s.com/Code/JavaAPI/java.net/newDatagramPacketbytebufintlengthInetAddressaddressintport.htm

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class MainClass {

  def main(args:Array[String]) {
    try {
      var ia:InetAddress = InetAddress.getByName("www.java2s.com")

      var ds:DatagramSocket = new DatagramSocket()

      var buffer:Array[Byte] = "hello".getBytes();

      //var dp:DatagramPacket = new DatagramPacket(buffer, buffer.length, ia, 80); //r>5
      var dp:DatagramPacket =  /*!*/ //r>5

      // Send the datagram packet
      ds.send(dp);
    } catch {
      case e => e.printStackTrace();
    }
  }
}

/*
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainClass {

  public static void main(String args[]) {
    try {

      InetAddress ia = InetAddress.getByName("www.java2s.com");

      DatagramSocket ds = new DatagramSocket();

      byte buffer[] = "hello".getBytes();
      DatagramPacket dp = new DatagramPacket(buffer, buffer.length, ia, 80);

      // Send the datagram packet
      ds.send(dp);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
*/






